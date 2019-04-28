package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;
import ru.psv4.tempdatchiki.model.Sensor;

import java.util.List;

@Service
public class ControllerService extends ReferenceService<Controller> {
    public ControllerService() {
        super(Controller.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Recipient> getControllersByRecipientUid(Controller controller) {
        return em.createQuery("SELECT e.recipient FROM RegRecipientController e " +
                "WHERE e.controller.uid = :controller ORDER BY e.subscribedDatetime", Recipient.class)
                .setParameter("controller", controller).getResultList();
    }
}
