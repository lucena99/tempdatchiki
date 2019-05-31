package ru.psv4.tempdatchiki.backend.service;

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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.psv4.tempdatchiki.backend.data.*;
import ru.psv4.tempdatchiki.backend.schedulers.ControllerEvent;
import ru.psv4.tempdatchiki.backend.schedulers.TempEvent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static ru.psv4.tempdatchiki.backend.data.NotificationType.Error;
import static ru.psv4.tempdatchiki.backend.data.NotificationType.*;

@Service
public class RecipientNotifier implements InitializingBean, EventBroker.EventListener {

    @Autowired
    private EventBroker eventBroker;

    @Autowired
    private SubscribtionService subscribtionService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private NotificationService messageService;

    private final ObjectMapper jacksonMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(RecipientNotifier.class);

    private static final DecimalFormat tempFormatter;
    static {
        tempFormatter = new DecimalFormat("#.#");
        DecimalFormatSymbols sep = new DecimalFormatSymbols(Locale.getDefault());
        sep.setDecimalSeparator('.');
        tempFormatter.setDecimalFormatSymbols(sep);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBroker.addListener(this);
    }

    @Override
    public void onEvent(ControllerEvent controllerEvent) {
        Controller controller = controllerEvent.getController();
        List<TempEvent> events = controllerEvent.getTempEvents();

        List<Subscription> subscriptions = subscribtionService.getRepository().findByController(controller);
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                Recipient recipient = subscription.getRecipient();
                for (TempEvent event : events) {
                    Optional<Notification> opMessage = messageService.getRepository()
                            .findByRecipientAndSensorLast(recipient, event.getSensor());
                    NotificationType nt = defineNotificationType(event);
                    switch (nt) {
                        case Normal: {
                            if (opMessage.isPresent() && opMessage.get().getType() != Normal) {
                                sendNotification(nt, recipient, event);
                            }
                            break;
                        }
                        case OverDown: {
                            if (!opMessage.isPresent() ||
                                    (opMessage.isPresent() && opMessage.get().getType() != OverDown)) {
                                sendNotification(nt, recipient, event);
                            }
                            break;
                        }
                        case OverUp: {
                            if (!opMessage.isPresent() ||
                                    (opMessage.isPresent() && opMessage.get().getType() != OverUp)) {
                                sendNotification(nt, recipient, event);
                            }
                            break;
                        }
                        case Error: {
                            if (!opMessage.isPresent() ||
                                    (opMessage.isPresent() && opMessage.get().getType() != Error)) {
                                sendNotification(nt, recipient, event);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private NotificationType defineNotificationType(TempEvent event) {
        Status status = event.getStatusNew();
        Sensor sensor = event.getSensor();
        double value = event.getValueNew();
        switch (event.getStatusNew()) {
            case Error:
                return NotificationType.Error;
            case Absence:
                return NotificationType.Error;
            case Normal:
                if (value < sensor.getMinValue()) {
                    return NotificationType.OverDown;
                } else if (value > sensor.getMaxValue()) {
                    return NotificationType.OverUp;
                } else {
                    return NotificationType.Normal;
                }
            default:
                throw new IllegalStateException("Can't define notification type");
        }
    }

    private void sendNotification(NotificationType nt, Recipient recipient, TempEvent event) {
        String jsonString;
        try {
            jsonString = createJsonString(nt, recipient, event);
            log.trace(jsonString);
        } catch (JsonProcessingException e) {
            log.error("Error create json", e);
            return;
        }

        final String authKey = settingService.getRepository()
                .findByName(Setting.EVENT_HUB_AUTHORIZATION_KEY).get().getValue();
        final String hubURL = settingService
                .getRepository().findByName(Setting.EVENT_HUB_URL).get().getValue();

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
                    log.info(String.format("Отправлено слушателю %s темп %s", recipient, event));

                    //сохранение в базу отметки об отправке уведомления
                    Notification message = messageService.createNew(null);
                    message.setType(nt);
                    message.setRecipient(recipient);
                    message.setSensor(event.getSensor());
                    messageService.getRepository().saveAndFlush(message);
                } else {
                    log.error("http {}; response: {}", http, statusLine);
                }
            }
        } catch (IOException e) {
            log.error("http error {}; {}", http, e.toString());
        }
    }

    private String createJsonString(NotificationType nt, Recipient recipient, TempEvent event) throws JsonProcessingException {
        ObjectNode rootNode = jacksonMapper.createObjectNode();
        rootNode.put("to", recipient.getFcmToken());
        ObjectNode notificationNode = jacksonMapper.createObjectNode();
        notificationNode.put("title", "Датчик температуры");
        notificationNode.put("body", formatString(nt, event));
        rootNode.set("notification", notificationNode);
        return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    public String formatString(NotificationType nt, TempEvent event) {
        Sensor sensor = event.getSensor();
        double value = event.getValueNew();
        switch (nt) {
            case OverDown:
                return String.format("%1$s.%2$s. %3$s(%4$s)",
                        sensor.getController().getName(), sensor.getName(),
                        tempFormatter.format(value), tempFormatter.format(sensor.getMinValue()));
            case OverUp:
                return String.format("%1$s.%2$s. %3$s(%4$s)",
                        sensor.getController().getName(), sensor.getName(),
                        tempFormatter.format(value), tempFormatter.format(sensor.getMaxValue()));
            case Normal:
                return String.format("%1$s.%2$s. %3$s(%4$s : %5$s)",
                        sensor.getController().getName(), sensor.getName(),
                        tempFormatter.format(value), tempFormatter.format(sensor.getMinValue()),
                        tempFormatter.format(sensor.getMaxValue()));
            case Error:
                return String.format("%1$s.%2$s. %3$s(%4$s : %5$s)",
                        sensor.getController().getName(), sensor.getName(),
                        "ERROR", tempFormatter.format(sensor.getMinValue()),
                        tempFormatter.format(sensor.getMaxValue()));
            default:
                return "Unknown";
        }
    }
}
