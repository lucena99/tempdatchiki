package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedEntityGraphs({@NamedEntityGraph(name = Recipient.ENTITY_GRAPTH_FULL, attributeNodes = {
        @NamedAttributeNode("subscriptions")
})})
public class Recipient extends Reference {

    public static final String ENTITY_GRAPTH_FULL = "Recipient.full";

    @Column(name = "fcm_token")
    private String fcmToken;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipient")
    @OrderBy("createdDatetime ASC")
    private List<Subscription> subscriptions;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
