package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;

import java.util.List;

@Service
public class RecipientService extends ReferenceService<Recipient> {

    public RecipientService() {
        super(Recipient.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Controller> getControllersByRecipientUid(Recipient recipient) {
        return em.createQuery("SELECT e.controller FROM RegRecipientController e " +
                "WHERE e.recipient = :recipient ORDER BY e.subscribedDatetime", Controller.class)
                .setParameter("recipient", recipient).getResultList();
    }
}
