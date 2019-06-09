package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Incident extends TdEntity implements IncidentTyped {

    @ManyToOne
    @JoinColumn(name = "sensor_uid")
    private Sensor sensor;

    private double value;

    @Column(name = "minvalue")
    private double minValue;

    @Column(name = "maxvalue")
    private double maxValue;

    @Column(name = "type_code")
    private int typeCode;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @Transient
    private IncidentType type;

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
        return type;
    }

    public void setType(IncidentType type) {
        this.type = type;
        this.typeCode = type.getCode();
    }

    @PostLoad
    private void fillTransients() {
        type = IncidentType.getByCode(typeCode);
    }
}
