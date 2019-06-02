package ru.psv4.tempdatchiki.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import ru.psv4.tempdatchiki.utils.IncidentDecisionResolver;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.psv4.tempdatchiki.backend.data.IncidentType.Error;
import static ru.psv4.tempdatchiki.backend.data.IncidentType.*;

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

    private ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setNameFormat("Notifications-%d")
            .setDaemon(true)
            .build());

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

    @PreDestroy
    public void destroy() {
        eventBroker.removeListener(this);
        executorService.shutdownNow();
        log.trace("Destroy " + getClass().getSimpleName());
    }


    @Override
    public void onEvent(ControllerEvent controllerEvent) {
        LocalDateTime startTime = LocalDateTime.now();
        executorService.submit(() -> {
            try {
                sendNotifications(controllerEvent);
            } catch (Exception e) {
                log.error("Error send notifictions startTime={} message={}", startTime, e.getMessage());
            }
        });
    }

    public void sendNotifications(ControllerEvent controllerEvent) {
        Controller controller = controllerEvent.getController();
        List<TempEvent> events = controllerEvent.getTempEvents();

        List<Subscription> subscriptions = subscribtionService.getRepository().findByController(controller);
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                Recipient recipient = subscription.getRecipient();
                for (TempEvent event : events) {
                    IncidentDecisionResolver.resolve(event,
                            (e) -> {
                                return messageService.getRepository()
                                        .findByRecipientAndSensorLast(recipient, e.getSensor());
                            },
                            (e, it) -> {
                                sendNotification(it, recipient, e);
                            });
                }
            }
        }
    }

    private void sendNotification(IncidentType nt, Recipient recipient, TempEvent event) {
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

    private String createJsonString(IncidentType nt, Recipient recipient, TempEvent event) throws JsonProcessingException {
        ObjectNode rootNode = jacksonMapper.createObjectNode();
        rootNode.put("to", recipient.getFcmToken());
        ObjectNode notificationNode = jacksonMapper.createObjectNode();
        notificationNode.put("title", "Датчик температуры");
        notificationNode.put("body", formatString(nt, event));
        rootNode.set("notification", notificationNode);
        return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    public String formatString(IncidentType nt, TempEvent event) {
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
