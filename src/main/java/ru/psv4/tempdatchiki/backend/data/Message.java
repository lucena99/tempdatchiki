package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message extends TdEntity {

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    @ManyToOne @JoinColumn(name = "recipient_uid")
    private Recipient recipient;

    @ManyToOne @JoinColumn(name = "sensor_uid")
    private Sensor sensor;

    @Column(name = "event_type_code")
    private int eventTypeCode;

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public LocalDateTime getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(LocalDateTime updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
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

    public Tag getEventType() {
        return Tag.getByCode(eventTypeCode);
    }
}
