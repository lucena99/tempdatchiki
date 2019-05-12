package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.ControllerRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ControllerService extends ReferenceService<Controller> implements FilterableCrudService<Controller> {

    @Autowired
    private ControllerRepository controllerRepository;

    public ControllerService() {
        super(Controller.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Recipient> getControllersByRecipientUid(Controller controller) {
        return em.createQuery("SELECT e.recipient FROM Subscription e " +
                "WHERE e.controller.uid = :controller ORDER BY e.subscribedDatetime", Recipient.class)
                .setParameter("controller", controller).getResultList();
    }

    @Override
    public Page<Controller> findAnyMatching(Optional<String> filter, Pageable pageable) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return controllerRepository.findByNameLikeIgnoreCase(repositoryFilter, pageable);
        } else {
            return find(pageable);
        }
    }

    @Override
    public long countAnyMatching(Optional<String> filter) {
        if (filter.isPresent()) {
            String repositoryFilter = "%" + filter.get() + "%";
            return controllerRepository.countByNameLikeIgnoreCase(repositoryFilter);
        } else {
            return count();
        }
    }

    public Page<Controller> find(Pageable pageable) {
        return controllerRepository.findBy(pageable);
    }

    @Override
    public JpaRepository<Controller, String> getRepository() {
        return controllerRepository;
    }

    @Override
    public Controller createNew(User currentUser) {
        Controller e = new Controller();
        e.setUid(UIDUtils.generate());
        e.setCreatedDatetime(LocalDateTime.now());
        return e;
    }

    @Override
    public Controller save(User currentUser, Controller entity) {
        try {
            return FilterableCrudService.super.save(currentUser, entity);
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException(
                    "There is already a controller with that name. Please select a unique name for the controller.");
        }

    }
}
