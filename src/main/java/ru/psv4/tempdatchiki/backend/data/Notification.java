package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification extends TdEntity {

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @ManyToOne @JoinColumn(name = "recipient_uid")
    private Recipient recipient;

    @ManyToOne @JoinColumn(name = "sensor_uid")
    private Sensor sensor;

    @Column(name = "notification_code")
    private int notificationCode;

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

    public void setType(NotificationType type) {
        this.notificationCode = type.getCode();
    }

    public NotificationType getType() {
        return NotificationType.getByCode(notificationCode);
    }
}
