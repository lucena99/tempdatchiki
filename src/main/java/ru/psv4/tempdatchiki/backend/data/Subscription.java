package ru.psv4.tempdatchiki.backend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.JsonSerializable;
import com.vaadin.flow.internal.JsonSerializer;
import elemental.json.Json;
import elemental.json.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Entity
public class Subscription extends TdEntity implements IReg<Subscription>, JsonSerializable {

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @ManyToOne @JoinColumn(name = "recipient_uid", nullable = false)
    @JsonBackReference
    @JsonIgnore
    private Recipient recipient;

    @ManyToOne @JoinColumn(name = "controller_uid", nullable = false)
    private Controller controller;

    @Column(name = "notify_over", nullable = false)
    private boolean notifyOver;

    @Column(name = "notify_error", nullable = false)
    private boolean notifyError;

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) { this.createdDatetime = createdDatetime; }

    @JsonIgnore
    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public boolean isNotifyOver() {
        return notifyOver;
    }

    public void setNotifyOver(boolean notifyOver) {
        this.notifyOver = notifyOver;
    }

    public boolean isNotifyError() {
        return notifyError;
    }

    public void setNotifyError(boolean notifyError) {
        this.notifyError = notifyError;
    }

    @Override
    public boolean equivalent(Subscription other) {
        return recipient.equals(other.recipient) && controller.equals(other.controller);
    }

    @Override
    public JsonObject toJson() {
        try {
            JsonObject json = Json.createObject();
            BeanInfo info = Introspector.getBeanInfo(getClass());
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if ("class".equals(pd.getName()) ||
                    pd.getName().equals("recipient")) {
                    continue;
                }
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    json.put(pd.getName(), JsonSerializer.toJson(reader.invoke(this)));
                }
            }

            return json;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Could not serialize object of type " + this.getClass()
                            + " to JsonValue",
                    e);
        }
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        return JsonSerializer.toObject(Subscription.class, value);
    }
}
