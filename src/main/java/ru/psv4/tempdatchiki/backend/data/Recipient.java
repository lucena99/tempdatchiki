package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedEntityGraphs({@NamedEntityGraph(name = Recipient.ENTITY_GRAPTH_FULL, attributeNodes = {
        @NamedAttributeNode("subscriptions")
})})
public class Recipient extends Reference {

    public static final String ENTITY_GRAPTH_FULL = "Recipient.full";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipient")
    private List<Subscription> subscriptions;

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
