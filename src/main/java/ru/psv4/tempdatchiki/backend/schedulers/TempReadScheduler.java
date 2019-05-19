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
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.backend.service.SettingService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.utils.Lazy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

    @Autowired
    private SettingService settingService;

    @Value("${temp.scheduler.active}")
    private boolean active;

    private static Logger log = LoggerFactory.getLogger(TempReadScheduler.class);

    @Scheduled(fixedRate = 1000)
    public void tempRead() {
        if (active) {
            LocalTime now = LocalTime.now();
            log.info(String.format("Temp read scheduler time = %s", now));
            List<Controller> controllers = controllerService.getList();
            controllers.stream().forEach(c -> tempReadAndNotifyRecipientsIfNeed(c));
        }
    }

    private void tempReadAndNotifyRecipientsIfNeed(Controller controller) {
        log.trace("Read controller {}", controller);
        Map<Integer, Float> tempMap = null;
        try {
            tempMap = getTemp(controller.getUrl());
            tempMap.entrySet().stream().forEach(e -> log.trace(e.toString()));

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
                        String jsonString = createJsonString(recipient, event);
                        log.trace(jsonString);
                        //sendEvent(jsonString);
                    } catch (JsonProcessingException e) {
                        log.error("Error", e);
                    }
                }
            }
        }
    }

    private void sendEvent(String jsonString) {
        final String authKey = settingService.getRepository().findByName(Setting.EVENT_HUB_AUTHORIZATION_KEY).get().getValue();
        final String hubURL = settingService.getRepository().findByName(Setting.EVENT_HUB_URL).get().getValue();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();

        HttpPost http = new HttpPost(hubURL);
        http.setConfig(requestConfig);
        http.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        http.setHeader(HttpHeaders.AUTHORIZATION, authKey);
        http.setEntity(new StringEntity(jsonString, "UTF-8"));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setConnectionManager(new BasicHttpClientConnectionManager())
                    .build()) {
            try (CloseableHttpResponse response = httpClient.execute(http)) {
                StatusLine statusLine = response.getStatusLine();
                int status = statusLine.getStatusCode();
                if (status == HttpStatus.SC_OK) {
                    log.trace("http {}; response: {}", http, statusLine);
                } else {
                    log.error("http {}; response: {}", http, statusLine);
                }
            }
        } catch (IOException e) {
            log.error("http {}; {}", http, e.toString());
        }
    }

    private ObjectMapper mapper = new ObjectMapper();

    private String createJsonString(Recipient recipient, Event event) throws JsonProcessingException {
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("to", "dTCzAHOae-Q:APA91bFrvlWe8upbXM1SkEs-_wISiZL706r9My6C1OjPk3ILySPmLfPSGrVWLNp" +
                "6b_vcxH5BH_05esouYDi3ZcWOcBl4vIL8hiDJnMm1t0h0aQ8Xp1ckFPlDJi1UA89H8wiVcVnpcNTi");
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