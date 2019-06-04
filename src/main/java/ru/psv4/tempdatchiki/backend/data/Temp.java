package ru.psv4.tempdatchiki.backend.data;

import com.vaadin.flow.component.JsonSerializable;
import ru.psv4.tempdatchiki.vaadin_json.TdJsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Temp extends TdEntity implements TdJsonSerializable {

    private double value;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_uid", nullable = false)
    @TdJsonIgnore
    private Sensor sensor;

    @Column(name = "updated_datetime", nullable = false)
    private LocalDateTime updatedDatetime;

    @Column(name = "status_code")
    private int statusCode;

    @Transient
    private Status status;

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
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.statusCode = status.getCode();
    }

    @PostLoad
    private void fillTransients() {
        status = Status.getByCode(statusCode);
    }
}
