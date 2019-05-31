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
import java.util.List;
import java.util.Optional;

import static ru.psv4.tempdatchiki.backend.data.EventType.Error;
import static ru.psv4.tempdatchiki.backend.data.EventType.*;

@Service
public class RecipientNotifier implements InitializingBean, EventBroker.EventListener {

    @Autowired
    private EventBroker eventBroker;

    @Autowired
    private SubscribtionService subscribtionService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private MessageService messageService;

    private final ObjectMapper jacksonMapper = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(RecipientNotifier.class);

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
                    Optional<Message> opMessage = messageService.getRepository()
                            .findByRecipientAndSensorLast(recipient, event.getSensor());
                    EventType state = event.getState();
                    switch (state) {
                        case Normal: {
                            if (opMessage.isPresent() && opMessage.get().getState() != Normal) {
                                sendEvent(recipient, event);
                            }
                            break;
                        }
                        case OverDown: {
                            if (!opMessage.isPresent() ||
                                    (opMessage.isPresent() && opMessage.get().getState() != OverDown)) {
                                sendEvent(recipient, event);
                            }
                            break;
                        }
                        case OverUp: {
                            if (!opMessage.isPresent() ||
                                    (opMessage.isPresent() && opMessage.get().getState() != OverUp)) {
                                sendEvent(recipient, event);
                            }
                            break;
                        }
                        case Error: {
                            if (!opMessage.isPresent() ||
                                    (opMessage.isPresent() && opMessage.get().getState() != Error)) {
                                sendEvent(recipient, event);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sendEvent(Recipient recipient, TempEvent event) {
        String jsonString;
        try {
            jsonString = createJsonString(recipient, event);
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

                    //сохранение в базу отметки об отправке
                    Message message = messageService.createNew(null);
                    message.setStateCode(event.getState().getCode());
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

    private String createJsonString(Recipient recipient, TempEvent event) throws JsonProcessingException {
        ObjectNode rootNode = jacksonMapper.createObjectNode();
        rootNode.put("to", recipient.getFcmToken());
        ObjectNode notificationNode = jacksonMapper.createObjectNode();
        notificationNode.put("title", "Датчик температуры");
        notificationNode.put("body", event.toString());
        rootNode.set("notification", notificationNode);
        return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }
}
