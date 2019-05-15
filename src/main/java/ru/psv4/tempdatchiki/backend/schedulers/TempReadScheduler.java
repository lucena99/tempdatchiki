package ru.psv4.tempdatchiki.backend.schedulers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.utils.Lazy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
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
    private SubscribtionService subscribtionService;

    private static Logger log = LogManager.getLogger(TempReadScheduler.class);

    @Scheduled(fixedRate = 1000)
    public void tempRead() {
        LocalTime now = LocalTime.now();
        log.info(String.format("Temp read scheduler time = %s", now));
        List<Controller> controllers = controllerService.getList();
        controllers.stream().forEach(c -> tempReadAndNotifyRecipientsIfNeed(c));
    }

    private void tempReadAndNotifyRecipientsIfNeed(Controller controller) {
        log.trace("Read controller {}", controller);
        Map<Integer, Float> tempMap = null;
        try {
            tempMap = getTemp(controller.getUrl());
            tempMap.entrySet().stream().forEach(e -> log.trace(e));

            Optional<List<Event>> opEvents = generateEventsIfNeed(controller, tempMap);
            if (opEvents.isPresent()) {
                notifyRecipients(controller, opEvents.get());
            }
        } catch (IOException | ParseException e) {
            log.error("Error", e);
        }
    }

    static DecimalFormat tempFormatter;
    static {
        tempFormatter = new DecimalFormat("#.#");
        DecimalFormatSymbols sep = new DecimalFormatSymbols(Locale.getDefault());
        sep.setDecimalSeparator('.');
        tempFormatter.setDecimalFormatSymbols(sep);
    }
    private class Event {
        EventType type;
        Sensor sensor;
        float currentValue;


        Event(EventType type, Sensor sensor, float currentValue) {
            this.type = type;
            this.sensor = sensor;
            this.currentValue = currentValue;
        }

        @Override
        public String toString() {
            switch (type) {
                case OverDown:
                    return String.format("%1$s.%2$s. %3$s(%4$s)",
                            sensor.getController().getName(), sensor.getName(),
                            tempFormatter.format(currentValue), tempFormatter.format(sensor.getMinValue()));
                case OverUp:
                    return String.format("%1$s.%2$s. %3$s(%4$s)",
                            sensor.getController().getName(), sensor.getName(),
                            tempFormatter.format(currentValue), tempFormatter.format(sensor.getMaxValue()));
                default:
                    return "Unknown";
            }
        }
    }

    ;

    private enum EventType {
        OverDown, OverUp, Error;
    }

    private Optional<List<Event>> generateEventsIfNeed(Controller controller, Map<Integer, Float> tempMap) {
        Lazy<List<Event>> events = new Lazy<>(() -> new ArrayList<>());
        List<Sensor> sensors = sensorService.getRepository().findByController(controller);
        for (Sensor sensor : sensors) {
            if (tempMap.containsKey(sensor.getNum())) {
                float currentValue = tempMap.get(sensor.getNum());
                if (currentValue > sensor.getMaxValue()) {
                    events.get().add(new Event(EventType.OverUp, sensor, currentValue));
                } else if (currentValue < sensor.getMinValue()) {
                    events.get().add(new Event(EventType.OverDown, sensor, currentValue));
                }
                //TODO: добавить показание error
            }
        }
        return events.getOptional();
    }

    private void notifyRecipients(Controller controller, List<Event> events) throws JsonProcessingException {
        List<Subscription> subscriptions = subscribtionService.getRepository().findByController(controller);
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                Recipient recipient = subscription.getRecipient();
                for (Event event : events) {
                    try {
                        log.info(createJsonString(recipient, event));
                    } catch (JsonProcessingException e) {
                        log.error("Error", e);
                    }
                }
            }
        }
    }

    private ObjectMapper mapper = new ObjectMapper();

    private String createJsonString(Recipient recipient, Event event) throws JsonProcessingException {
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("to", recipient.getUid());
        ObjectNode notificationNode = mapper.createObjectNode();
        notificationNode.put("title", "Датчик температуры");
        notificationNode.put("body", event.toString());
        rootNode.set("notification", notificationNode);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private Pattern pattern = Pattern.compile("(?<num>[\\d\\s]+)#(?<val>-?[\\d\\s]+\\.?[\\d\\s]*)");
    private DecimalFormat floatFormat;

    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        floatFormat = new DecimalFormat();
        floatFormat.setDecimalFormatSymbols(symbols);
    }

    private Map<Integer, Float> getTemp(String urlToRead) throws IOException, ParseException {
        Map<Integer, Float> map = new LinkedHashMap<>();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            Matcher m = pattern.matcher(line.trim());
            if (m.find()) {
                Integer num = new Integer(m.group("num").trim());
                String val = m.group("val").trim();
                if (!StringUtils.isEmpty(val))
                    map.put(new Integer(m.group("num")), floatFormat.parse(val).floatValue());
            }
        }
        return map;
    }
}