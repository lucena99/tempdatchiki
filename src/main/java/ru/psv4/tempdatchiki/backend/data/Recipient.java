package ru.psv4.tempdatchiki.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedEntityGraphs({@NamedEntityGraph(name = Recipient.ENTITY_GRAPTH_FULL, attributeNodes = {
        @NamedAttributeNode("subscriptions")
})})
public class Recipient extends Reference {

    public static final String ENTITY_GRAPTH_FULL = "Recipient.full";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "recipient")
    @JsonManagedReference
    @JsonIgnore
    private List<Subscription> subscriptions;

    @JsonIgnore
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

}
