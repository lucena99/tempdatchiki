package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Incident extends TdEntity implements IncidentTyped {

    @ManyToOne @JoinColumn(name = "sensor_uid")
    private Sensor sensor;

    private double value;

    @Column(name = "minvalue")
    private double minValue;

    @Column(name = "maxvalue")
    private double maxValue;

    @Column(name = "incident_code")
    private int incidentCode;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setType(IncidentType type) {
        this.incidentCode = type.getCode();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
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

    public IncidentType getType() {
        return IncidentType.getByCode(incidentCode);
    }
}
