package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message extends TdEntity {

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @ManyToOne @JoinColumn(name = "recipient_uid")
    private Recipient recipient;

    @ManyToOne @JoinColumn(name = "sensor_uid")
    private Sensor sensor;

    @Column(name = "state_code")
    private int stateCode;

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public EventType getState() {
        return EventType.getByCode(stateCode);
    }
}
