package ru.psv4.tempdatchiki.backend.schedulers;

import ru.psv4.tempdatchiki.backend.data.Controller;

import java.util.List;

public class ControllerEvent {

    private Controller controller;
    private List<TempEvent> tempEvents;

    public ControllerEvent(Controller controller, List<TempEvent> tempEvents) {
        this.controller = controller;
        this.tempEvents = tempEvents;
    }

    public Controller getController() {
        return controller;
    }

    public List<TempEvent> getTempEvents() {
        return tempEvents;
    }
}
