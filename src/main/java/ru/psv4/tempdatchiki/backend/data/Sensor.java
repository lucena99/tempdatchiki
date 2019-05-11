package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Sensor extends Reference {

    @ManyToOne
    @JoinColumn(name = "controller_uid", nullable = false)
    private Controller controller;

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
