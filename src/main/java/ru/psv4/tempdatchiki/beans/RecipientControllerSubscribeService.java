package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;
import ru.psv4.tempdatchiki.model.RecipientControllerRegset;
import ru.psv4.tempdatchiki.model.RegRecipientController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class RecipientControllerSubscribeService extends TdEntityService<RegRecipientController> {

    public RecipientControllerSubscribeService() { super(RegRecipientController.class); }

    @Transactional(propagation = Propagation.REQUIRED)
    public void subscribe(Recipient recipient, Controller controller, boolean notifyOver, boolean notifyError) {
        RecipientControllerRegset regset = new RecipientControllerRegset(
                em.createQuery("SELECT e FROM RegRecipientController e " +
                        "WHERE e.recipient = :recipient AND e.controller = :controller", RegRecipientController.class).getResultList()
        );
        RegRecipientController reg = new RegRecipientController();
        reg.setRecipient(recipient);
        reg.setController(controller);
        reg.setNotifyOver(notifyOver);
        reg.setNotifyError(notifyError);
        regset.add(reg);
        regset.save(em);
    }
}
