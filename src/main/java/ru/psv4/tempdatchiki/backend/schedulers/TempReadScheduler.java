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
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.psv4.tempdatchiki.backend.data.Tag.Normal;

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

    @Autowired
    private MessageService messageService;

    @org.springframework.beans.factory.annotation.Value("${temp.scheduler.active}")
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

            List<Temp> tempList = processMeasurement(controller, tempMap);

            notifyRecipientsIfNeed(controller, tempList);
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
    private class Temp {
        Tag tag;
        Sensor sensor;
        float currentValue;


        Temp(Tag tag, Sensor sensor, float currentValue) {
            this.tag = tag;
            this.sensor = sensor;
            this.currentValue = currentValue;
        }

        @Override
        public String toString() {
            switch (tag) {
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

    private List<Temp> processMeasurement(Controller controller, Map<Integer, Float> tempMap) {
        Lazy<List<Temp>> events = new Lazy<>(() -> new ArrayList<>());
        List<Sensor> sensors = sensorService.getRepository().findByController(controller);
        for (Sensor sensor : sensors) {
            if (tempMap.containsKey(sensor.getNum())) {
                float currentValue = tempMap.get(sensor.getNum());
                if (currentValue > sensor.getMaxValue()) {
                    events.get().add(new Temp(Tag.OverUp, sensor, currentValue));
                } else if (currentValue < sensor.getMinValue()) {
                    events.get().add(new Temp(Tag.OverDown, sensor, currentValue));
                } else {
                    events.get().add(new Temp(Normal, sensor, currentValue));
                }
                //TODO: добавить показание error
            }
        }
        return events.get();
    }

    private void notifyRecipientsIfNeed(Controller controller, List<Temp> tempList) throws JsonProcessingException {
        List<Subscription> subscriptions = subscribtionService.getRepository().findByController(controller);
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                Recipient recipient = subscription.getRecipient();
                for (Temp temp : tempList) {
                    try {
                        Optional<Message> opMessage = messageService.getRepository().findByRecipientAndSensorLast(recipient, temp.sensor);
                        Tag tag = temp.tag;
                        switch (tag) {
                            case Normal: {
                                if (!opMessage.isPresent() || opMessage.get().getEventType() == Normal) {
                                    break;
                                } else {
                                    Message message = opMessage.get();
                                    if (message.getEventType() != Normal) {
                                        sendEvent(recipient, temp);
                                    }
                                }
                            }
                            case
                        }
                        //sendEvent(jsonString);
                    } catch (JsonProcessingException e) {
                        log.error("Error", e);
                    }
                }
            }
        }
    }

    private void sendEvent(Recipient recipient, Temp temp) {
        String jsonString = createJsonString(recipient, temp);
        log.trace(jsonString);

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

        Message message = messageService.createNew(null);
        //TODO:save
    }

    private ObjectMapper mapper = new ObjectMapper();

    private String createJsonString(Recipient recipient, Temp event) throws JsonProcessingException {
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("to", recipient.getFcmToken());
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