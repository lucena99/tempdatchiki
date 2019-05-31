package ru.psv4.tempdatchiki.backend.service;

import org.springframework.stereotype.Service;
import ru.psv4.tempdatchiki.backend.schedulers.ControllerEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EventBroker {

    private List<EventListener> listeners = Collections.synchronizedList(new ArrayList<>());

    public void notify(ControllerEvent controllerEvent) {
        listeners.forEach(l -> l.onEvent(controllerEvent));
    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.add(listener);
    }

    @FunctionalInterface
    public static interface EventListener {
        public void onEvent(ControllerEvent controllerEvent);
    }
}
