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

    private static final Pattern controllerResponsePattern = Pattern.compile("(?<num>[\\d\\s]+)#(?<val>(-?[\\d\\s]+\\.?[\\d\\s]*)|(ERROR))");
    private static final DecimalFormat tempFormat;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        tempFormat = new DecimalFormat();
        tempFormat.setDecimalFormatSymbols(symbols);
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
            log.trace("Start read controller {}", controller);
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
                    Status error = Status.Error;
                    if (temp.getStatus() != error) {
                        event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), error, 0d);
                    }
                    temp.setStatus(error);
                } else if (values.isBlank(num)) {
                    Status absence = Status.Off;
                    if (temp.getStatus() != absence) {
                        event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), absence, 0d);
                    }
                    temp.setStatus(absence);
                } else {
                    Status normal = Status.On;
                    double valueNew = values.getValue(num).get();
                    if (temp.getStatus() != normal ||
                        temp.getValue() != valueNew) {
                        event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), normal, valueNew);
                    }
                    temp.setStatus(normal);
                    temp.setValue(valueNew);
                }
            } else {
                Status absence = Status.Off;
                if (temp.getStatus() != absence) {
                    event = new TempEvent(sensor, temp.getStatus(), temp.getValue(), absence, 0d);
                }
                temp.setStatus(absence);
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
        List<Integer> blanks;
        Map<Integer, Double> values;

        public TempValues(LocalDateTime time) {
            this.time = time;
        }

        void addValue(Integer num, Double value) {
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

        void addBlank(Integer num) {
            if (blanks == null) {
                blanks = new ArrayList<>();
            }
            blanks.add(num);
        }

        boolean contains(Integer num) {
            return (values != null && values.containsKey(num)) ||
                    (errors != null && errors.contains(num)) ||
                    (blanks != null && blanks.contains(num));
        }

        boolean isError(Integer num) {
            return errors != null && errors.contains(num);
        }

        boolean isBlank(Integer num) {
            return blanks != null && blanks.contains(num);
        }

        Optional<Double> getValue(Integer num) {
            return Optional.ofNullable(values != null && values.containsKey(num) ? values.get(num) : null);
        }

        void trace(Logger log) {
            if (values != null) {
                values.entrySet().stream().forEach(e -> log.trace(e.toString()));
            }
            if (errors != null) {
                errors.stream().forEach(e -> log.trace("ERROR #{}", e));
            }
            if (blanks != null) {
                blanks.stream().forEach(e -> log.trace("BLANK #{}", e));
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
                        values.addValue(num, tempFormat.parse(val).doubleValue());
                    }
                }
            }
        }
        return values;
    }
}