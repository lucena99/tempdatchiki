package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;
import ru.psv4.tempdatchiki.model.Subscription;
import ru.psv4.tempdatchiki.model.SubscriptionRegset;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscribtionService extends TdEntityService<Subscription> {

    public SubscribtionService() { super(Subscription.class); }

    @Transactional(propagation = Propagation.REQUIRED)
    public Subscription create(Recipient recipient, Controller controller, boolean notifyOver, boolean notifyError) {
        Subscription s = new Subscription();
        s.setUid(UIDUtils.generate());
        s.setRecipient(recipient);
        s.setController(controller);
        s.setCreatedDatetime(LocalDateTime.now());
        s.setNotifyOver(notifyOver);
        s.setNotifyError(notifyError);
        return em.merge(s);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<Subscription> get(Recipient r, Controller c) {
        TypedQuery<Subscription> query = em.createQuery("SELECT e FROM " + eClass.getName() +
                " e WHERE e.recipient = :recipient AND e.controller = :controller", Subscription.class);
        query.setParameter("recipient", r);
        query.setParameter("controller", c);
        return query.getResultList().stream().findFirst();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Subscription> getList() {
        TypedQuery<Subscription> query = em.createQuery("SELECT e FROM " + eClass.getName() +
                " e ORDER BY e.createdDatetime", Subscription.class);
        return query.getResultList();
    }
}
