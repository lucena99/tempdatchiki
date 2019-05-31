package ru.psv4.tempdatchiki.backend.schedulers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.psv4.tempdatchiki.backend.data.*;
import ru.psv4.tempdatchiki.backend.service.*;
import ru.psv4.tempdatchiki.utils.Lazy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.psv4.tempdatchiki.backend.data.EventType.Error;
import static ru.psv4.tempdatchiki.backend.data.EventType.*;

@Component
public class TempReadScheduler {

    @Autowired
    private ControllerService controllerService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private TempService tempService;

    @Autowired
    private EventBroker eventBroker;

    @Value("${temp.scheduler.active}")
    private boolean active;

    private ObjectMapper jacksonMapper = new ObjectMapper();

    private Pattern controllerResponsePattern = Pattern.compile("(?<num>[\\d\\s]+)#(?<val>-?[\\d\\s]+\\.?[\\d\\s]*)");
    private DecimalFormat floatFormat;
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        floatFormat = new DecimalFormat();
        floatFormat.setDecimalFormatSymbols(symbols);
    }

    private static Logger log = LoggerFactory.getLogger(TempReadScheduler.class);

    @Scheduled(fixedRate = 1000)
    public void tempRead() {
        if (active) {
            LocalTime now = LocalTime.now();
            log.info(String.format("Temp read scheduler time = %s", now));
            List<Controller> controllers = controllerService.getList();
            controllers.stream().forEach(c -> tempReadAndNotifyIfNeed(c));
        }
    }

    private void tempReadAndNotifyIfNeed(Controller controller) {
        try {
            log.trace("Reading controller {}", controller);
            TempValues values = readTemp(controller.getUrl());
            values.trace(log);
            log.trace("Successful read controller {}", controller);

            List<TempEvent> events = updateTempsAndCreateEvents(controller, values);
            if (!events.isEmpty()) {
                eventBroker.notify(new ControllerEvent(controller, events));
            }
        } catch (IOException | ParseException e) {
            log.error("Error read controller {} Message {}", controller, e.getMessage());
        }
    }

    private List<TempEvent> updateTempsAndCreateEvents(Controller controller, TempValues values) {
        Lazy<List<TempEvent>> events = new Lazy<>(() -> new ArrayList<>());
        List<Sensor> sensors = sensorService.getRepository().findByController(controller);
        for (Sensor sensor : sensors) {
            Temp temp = sensor.getTemp();
            if (temp == null) {
                temp = tempService.createNew(null);
                temp.setSensor(sensor);
            }
            TempEvent event = null;
            Integer num = sensor.getNum();
            if (values.contains(num)) {
                if (values.isError(num)) {
                    if (temp.getStatus() != Status.Error) {
                        event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), Status.Error, 0d);
                    }
                    temp.setStatus(Status.Error);
                } else {
                    double valueNew = values.getValue(num).get();
                    if (temp.getStatus() != Status.Normal ||
                        temp.getValue() != valueNew) {
                        event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), Status.Normal, valueNew);
                    }
                    temp.setStatus(Status.Normal);
                    temp.setValue(valueNew);
                }
            } else {
                if (temp.getStatus() != Status.Absence) {
                    event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), Status.Absence, 0d);
                }
                temp.setStatus(Status.Absence);
            }

            temp.setUpdatedDatetime(values.time);
            tempService.getRepository().saveAndFlush(temp);

            if (event != null) {
                events.get().add(event);
            }
        }
        return events.get();
    }

    private class TempValues {

        LocalDateTime time;
        List<Integer> errors;
        Map<Integer, Float> values;

        public TempValues(LocalDateTime time) {
            this.time = time;
        }

        void addValue(Integer num, Float value) {
            if (values == null) {
                values = new HashMap<>();
            }
            values.put(num, value);
        }

        void addError(Integer num) {
            if (errors == null) {
                errors = new ArrayList<>();
            }
            errors.add(num);
        }

        boolean contains(Integer num) {
            return (values != null && values.containsKey(num)) || (errors != null && errors.contains(num));
        }

        boolean isError(Integer num) {
            return errors != null && errors.contains(num);
        }

        Optional<Float> getValue(Integer num) {
            return Optional.ofNullable(values != null && values.containsKey(num) ? values.get(num) : null);
        }

        void trace(Logger log) {
            if (values != null) {
                values.entrySet().stream().forEach(e -> log.trace(e.toString()));
            }
            if (errors != null) {
                errors.stream().forEach(e -> log.trace("ERROR #{}", e));
            }
        }
    }

    private TempValues readTemp(String urlToRead) throws IOException, ParseException {
        TempValues values = new TempValues(LocalDateTime.now());
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            Matcher m = controllerResponsePattern.matcher(line.trim());
            if (m.find()) {
                Integer num = new Integer(m.group("num").trim());
                String val = m.group("val").trim();
                if (!StringUtils.isEmpty(val)) {
                    if (val.equals("ERROR")) {
                        values.addError(num);
                    } else {
                        values.addValue(num, floatFormat.parse(val).floatValue());
                    }
                }
            }
        }
        return values;
    }
}