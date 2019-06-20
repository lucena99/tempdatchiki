package ru.psv4.tempdatchiki.backend.data;

import ru.psv4.tempdatchiki.ui.vaadin_json.TdJsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Sensor extends Reference implements TdJsonSerializable {

    @ManyToOne
    @JoinColumn(name = "controller_uid", nullable = false)
    @TdJsonIgnore
    private Controller controller;

    private int num;

    @Column(name = "minvalue")
    private double minValue;

    @Column(name = "maxvalue")
    private double maxValue;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "sensor")
    private Temp temp;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "sensor")
    private List<Incident> incidents;

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Temp getTemp() {
        return temp;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }
}
