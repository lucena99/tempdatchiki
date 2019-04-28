package ru.psv4.tempdatchiki.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class RegRecipientController extends TdEntity implements IReg<RegRecipientController> {

    @Column(name = "subscribed_datetime")
    private LocalDateTime subscribedDatetime;

    @ManyToOne @JoinColumn(name = "recipient_uid", nullable = false)
    private Recipient recipient;

    @ManyToOne @JoinColumn(name = "controller_uid", nullable = false)
    private Controller controller;

    private boolean notifyOver;

    private boolean notifyError;

    public LocalDateTime getSubscribedDatetime() {
        return subscribedDatetime;
    }

    public void setSubscribedDatetime(LocalDateTime subscribedDatetime) {
        this.subscribedDatetime = subscribedDatetime;
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
    public boolean equivalent(RegRecipientController other) {
        return recipient.equals(other.recipient) && controller.equals(other.controller);
    }
}
