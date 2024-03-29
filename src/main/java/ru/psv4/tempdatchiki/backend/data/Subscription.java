package ru.psv4.tempdatchiki.backend.data;

import ru.psv4.tempdatchiki.ui.vaadin_json.TdJsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Subscription extends TdEntity implements IReg<Subscription>, TdJsonSerializable {

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_uid", nullable = false)
    @TdJsonIgnore
    private Recipient recipient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "controller_uid", nullable = false)
    private Controller controller;

    @Column(name = "notify_out", nullable = false)
    private boolean notifyOut;

    @Column(name = "notify_error", nullable = false)
    private boolean notifyError;

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

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public boolean isNotifyOut() {
        return notifyOut;
    }

    public void setNotifyOut(boolean notifyOut) {
        this.notifyOut = notifyOut;
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
}
