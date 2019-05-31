package ru.psv4.tempdatchiki.backend.schedulers;

import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Status;

public class TempEvent {

    private Sensor sensor;
    private Status statusOld;
    private Status statusNew;
    private double valueOld;
    private double valueNew;

    public TempEvent(Sensor sensor, Status statusOld, double valueOld, Status statusNew, double valueNew) {
        this.sensor = sensor;
        this.statusOld = statusOld;
        this.valueOld = valueOld;
        this.statusNew = statusNew;
        this.valueNew = valueNew;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Status getStatusOld() {
        return statusOld;
    }

    public Status getStatusNew() {
        return statusNew;
    }

    public double getValueOld() {
        return valueOld;
    }

    public double getValueNew() {
        return valueNew;
    }
}