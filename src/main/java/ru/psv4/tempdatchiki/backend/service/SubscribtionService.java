package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.SubscriptionRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscribtionService extends TdEntityService<Subscription> implements CrudService<Subscription> {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public SubscribtionService() { super(Subscription.class); }

    @Transactional(propagation = Propagation.REQUIRED)
    public Subscription create(Recipient recipient, Controller controller, boolean notifyOut, boolean notifyError) {
        Subscription s = new Subscription();
        s.setUid(UIDUtils.generate());
        s.setRecipient(recipient);
        s.setController(controller);
        s.setCreatedDatetime(LocalDateTime.now());
        s.setNotifyOut(notifyOut);
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

    @Override
    public SubscriptionRepository getRepository() {
        return subscriptionRepository;
    }
}
