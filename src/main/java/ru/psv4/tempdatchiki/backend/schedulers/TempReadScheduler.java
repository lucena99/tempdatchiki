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
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.psv4.tempdatchiki.backend.data.State.*;

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

    private static DecimalFormat tempFormatter;
    static {
        tempFormatter = new DecimalFormat("#.#");
        DecimalFormatSymbols sep = new DecimalFormatSymbols(Locale.getDefault());
        sep.setDecimalSeparator('.');
        tempFormatter.setDecimalFormatSymbols(sep);
    }

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
        try {
            TempValues values = readTemp(controller.getUrl());
            values.trace(log);
            List<AnalyzedValue> analyzedValues = analyzeTempValues(controller, values);
            notifyRecipientsIfNeed(controller, analyzedValues);
        } catch (IOException | ParseException e) {
            log.error("Error read controller {} Message {}", controller, e.getMessage());
        }
    }

    private class AnalyzedValue {

        State state;
        Sensor sensor;
        float value;

        AnalyzedValue(State tag, Sensor sensor, float value) {
            this.state = tag;
            this.sensor = sensor;
            this.value = value;
        }

        @Override
        public String toString() {
            switch (state) {
                case OverDown:
                    return String.format("%1$s.%2$s. %3$s(%4$s)",
                            sensor.getController().getName(), sensor.getName(),
                            tempFormatter.format(value), tempFormatter.format(sensor.getMinValue()));
                case OverUp:
                    return String.format("%1$s.%2$s. %3$s(%4$s)",
                            sensor.getController().getName(), sensor.getName(),
                            tempFormatter.format(value), tempFormatter.format(sensor.getMaxValue()));
                default:
                    return "Unknown";
            }
        }
    }

    private List<AnalyzedValue> analyzeTempValues(Controller controller, TempValues values) {
        Lazy<List<AnalyzedValue>> events = new Lazy<>(() -> new ArrayList<>());
        List<Sensor> sensors = sensorService.getRepository().findByController(controller);
        for (Sensor sensor : sensors) {
            Integer num = sensor.getNum();
            if (values.contains(num)) {
                if (values.isError(num)) {
                    events.get().add(new AnalyzedValue(State.Error, sensor, 0));
                } else {
                    float value = values.getValue(num).get();
                    if (value > sensor.getMaxValue()) {
                        events.get().add(new AnalyzedValue(State.OverUp, sensor, value));
                    } else if (value < sensor.getMinValue()) {
                        events.get().add(new AnalyzedValue(State.OverDown, sensor, value));
                    } else {
                        events.get().add(new AnalyzedValue(Normal, sensor, value));
                    }
                }
            }
        }
        return events.get();
    }

    private void notifyRecipientsIfNeed(Controller controller, List<AnalyzedValue> tempList) throws JsonProcessingException {
        List<Subscription> subscriptions = subscribtionService.getRepository().findByController(controller);
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                Recipient recipient = subscription.getRecipient();
                for (AnalyzedValue temp : tempList) {
                    Optional<Message> opMessage = messageService.getRepository().findByRecipientAndSensorLast(recipient, temp.sensor);
                    State tag = temp.state;
                    switch (tag) {
                        case Normal: {
                            if (opMessage.isPresent() && opMessage.get().getState() != Normal) {
                                sendEvent(recipient, temp);
                            }
                            break;
                        }
                        case OverDown: {
                            if (!opMessage.isPresent() || (opMessage.isPresent() && opMessage.get().getState() != OverDown)) {
                                sendEvent(recipient, temp);
                            }
                            break;
                        }
                        case OverUp: {
                            if (!opMessage.isPresent() || (opMessage.isPresent() && opMessage.get().getState() != OverUp)) {
                                sendEvent(recipient, temp);
                            }
                            break;
                        }
                        case Error: {
                            if (!opMessage.isPresent() || (opMessage.isPresent() && opMessage.get().getState() != Error)) {
                                sendEvent(recipient, temp);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sendEvent(Recipient recipient, AnalyzedValue temp) {
        String jsonString;
        try {
            jsonString = createJsonString(recipient, temp);
            log.trace(jsonString);
        } catch (JsonProcessingException e) {
            log.error("Error create json", e);
            return;
        }

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
                    log.info(String.format("Отправлено слушателю %s темп %s", recipient, temp));

                    //сохранение в базу отметки об отправке
                    Message message = messageService.createNew(null);
                    message.setStateCode(temp.state.getCode());
                    message.setRecipient(recipient);
                    message.setSensor(temp.sensor);
                    messageService.getRepository().saveAndFlush(message);
                } else {
                    log.error("http {}; response: {}", http, statusLine);
                }
            }
        } catch (IOException e) {
            log.error("http error {}; {}", http, e.toString());
        }
    }

    private String createJsonString(Recipient recipient, AnalyzedValue event) throws JsonProcessingException {
        ObjectNode rootNode = jacksonMapper.createObjectNode();
        rootNode.put("to", recipient.getFcmToken());
        ObjectNode notificationNode = jacksonMapper.createObjectNode();
        notificationNode.put("title", "Датчик температуры");
        notificationNode.put("body", event.toString());
        rootNode.set("notification", notificationNode);
        return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private class TempValues {

        List<Integer> errors;
        Map<Integer, Float> values;

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
        TempValues values = new TempValues();
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