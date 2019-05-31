package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Temp extends TdEntity {

    private double value;

    @OneToOne @JoinColumn(name = "sensor_uid", nullable = false)
    private Sensor sensor;

    @Column(name = "updated_datetime", nullable = false)
    private LocalDateTime updatedDatetime;

    @Column(name = "status_code")
    private int statusCode;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public LocalDateTime getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(LocalDateTime updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Status getStatus() {
        return Status.getByCode(statusCode);
    }

    public void setStatus(Status status) {
        this.statusCode = status.getCode();
    }
}
