package ru.psv4.tempdatchiki.backend.schedulers;

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
import ru.psv4.tempdatchiki.utils.UIDUtils;

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

    @Value("${temp.scheduler.http.attemps}")
    private int attempts;

    private static final Pattern controllerResponsePattern = Pattern.compile("(?<num>[\\d\\s]+)#(?<val>(\\s?-?[\\d\\s]+\\.?[\\d\\s]*)|(ERROR))");
    private static final DecimalFormat tempFormat;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        tempFormat = new DecimalFormat();
        tempFormat.setDecimalFormatSymbols(symbols);
    }

    private static Logger log = LoggerFactory.getLogger(TempReadScheduler.class);

    private class ReadContext {
        String responseBody;
    }

    @Scheduled(fixedRate = 5000)
    public void tempRead() {
        if (active) {
            LocalTime now = LocalTime.now();
            log.info(String.format("Temp read scheduler time = %s", now));
            List<Controller> controllers = controllerService.getList();
            controllers.stream().forEach(c -> {
                ReadContext readContext = null;
                try {
                    readContext = new ReadContext();
                    tempReadAndNotifyIfNeed(c, readContext);
                } catch (Exception e) {
                    log.error("Error temp read {}", c);
                    log.error(System.lineSeparator() + readContext.responseBody);
                }
            });
        }
    }

    private void tempReadAndNotifyIfNeed(Controller controller, ReadContext readContext) {
        String urlToRead = controller.getUrl();
        TempValues values;
        try {
            log.trace("Start read controller {}", controller);
            values = readTemp(urlToRead, readContext);
            values.trace(log);
            log.trace("Successful read controller {}", controller);
        } catch (IOException e) {
            log.error("Error http-request: {} message: {}", urlToRead, e.getMessage());
            values = new TempValues(LocalDateTime.now());
            List<Sensor> sensors = sensorService.getRepository().findByController(controller);
            for (Sensor sensor : sensors) {
                values.setStatus(sensor.getNum(), Status.Unreachable);
            }
        }

        List<TempEvent> events = updateTempsAndCreateEvents(controller, values);
        if (!events.isEmpty()) {
            eventBroker.notify(new ControllerEvent(controller, events));
        }
    }

    private List<TempEvent> updateTempsAndCreateEvents(Controller controller, TempValues values) {
        Lazy<List<TempEvent>> events = new Lazy<>(() -> new ArrayList<>());
        List<Sensor> sensors = sensorService.getRepository().findByController(controller);
        for (Sensor sensor : sensors) {
            Temp temp = sensor.getTemp();
            if (temp == null) {
                temp = new Temp();
                temp.setUid(UIDUtils.generate());
                temp.setUpdatedDatetime(LocalDateTime.now());
                temp.setSensor(sensor);
            }
            TempEvent event = null;
            Integer num = sensor.getNum();
            if (values.contains(num)) {
                Status statusNew = values.getStatus(num);
                if (statusNew == Status.On) {
                    double valueNew = values.getValue(num).get();
                    if (temp.getStatus() != statusNew || temp.getValue() != valueNew) {
                        event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), statusNew, valueNew);
                    }
                    temp.setValue(valueNew);
                    temp.setStatus(statusNew);
                } else {
                    if (temp.getStatus() != statusNew) {
                        event = new TempEvent(sensor, temp.getStatus(), statusNew);
                    }
                    temp.setStatus(statusNew);
                }
            } else {
                Status notfound = Status.NotFound;
                if (temp.getStatus() != notfound) {
                    event = new TempEvent(sensor, temp.getStatus(), notfound);
                }
                temp.setStatus(notfound);
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
        Map<Integer, Double> values = new HashMap<>();
        Map<Integer, Status> statuses = new HashMap<>();

        public TempValues(LocalDateTime time) {
            this.time = time;
        }

        void addValue(Integer num, Double value) {
            values.put(num, value);
            statuses.put(num, Status.On);
        }

        void setStatus(Integer num, Status value) {
            statuses.put(num, value);
        }

        Status getStatus(Integer num) {
            return statuses.get(num);
        }

        boolean contains(Integer num) {
            return statuses.containsKey(num);
        }

        Optional<Double> getValue(Integer num) {
            return Optional.ofNullable(values != null && values.containsKey(num) ? values.get(num) : null);
        }

        void trace(Logger log) {
            values.entrySet().stream().forEach(e -> log.trace(e.toString()));
            statuses.entrySet().stream().filter(e -> e.getValue() != Status.On)
                    .forEach(e -> log.trace("{} #{}",e.getValue(), e.getKey()));
        }
    }

    private TempValues readTemp(String urlToRead, ReadContext readContext) throws IOException {
        TempValues values = new TempValues(LocalDateTime.now());

        int attempts = this.attempts;
        String response = null;
        while (response == null) {
            try {
                --attempts;
                response = execHttpRequest(urlToRead);
                readContext.responseBody  = new String(response);
            } catch (IOException e) {
                log.info("Осталось количество попыток {} прочитать {}", attempts, urlToRead);
                if (attempts == 0) {
                    throw e; //попытки кончились
                }
            }
        }

        for (String line : response.split(System.lineSeparator())) {
            Matcher m = controllerResponsePattern.matcher(line.trim());
            if (m.find()) {
                Integer num = new Integer(m.group("num").trim());
                String val = m.group("val").trim();
                if (!StringUtils.isEmpty(val)) {
                    if (val.equals("ERROR")) {
                        values.setStatus(num, Status.Error);
                    } else {
                        try {
                            values.addValue(num, tempFormat.parse(val).doubleValue());
                        } catch (ParseException e) {
                            values.setStatus(num, Status.SystemError);
                        }
                    }
                }
            }
        }
        return values;
    }

    private String execHttpRequest(String urlToRead) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10*1000);
        conn.setReadTimeout(10*1000);
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            if (sb.length() != 0) {
                sb.append(System.lineSeparator());
            }
            sb.append(line);
        }
        return sb.toString();
    }
}