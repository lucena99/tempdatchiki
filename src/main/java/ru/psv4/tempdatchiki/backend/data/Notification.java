package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification extends TdEntity implements IncidentTyped {

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @ManyToOne @JoinColumn(name = "recipient_uid")
    private Recipient recipient;

    @ManyToOne @JoinColumn(name = "sensor_uid")
    private Sensor sensor;

    @Column(name = "type_code")
    private int typeCode;

    @Transient
    private IncidentType type;

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

    public void setType(IncidentType type) {
        this.typeCode = type.getCode();
    }

    public IncidentType getType() {
        return IncidentType.getByCode(typeCode);
    }

    @PostLoad
    private void fillTransients() {
        type = IncidentType.getByCode(typeCode);
    }
}
