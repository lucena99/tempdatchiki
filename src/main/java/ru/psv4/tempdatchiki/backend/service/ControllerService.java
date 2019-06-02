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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ControllerService extends ReferenceService<Controller> implements CrudService<Controller> {

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

    public Page<Controller> findAnyMatching(Optional<String> optionalFilter, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return controllerRepository.findByNameContainingIgnoreCase(optionalFilter.get(), pageable);
        } else {
            return controllerRepository.findAll(pageable);
        }
    }

    public long countAnyMatching(Optional<String> optionalFilter) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return controllerRepository.countByNameContainingIgnoreCase(optionalFilter.get());
        } else {
            return controllerRepository.count();
        }
    }

    public Page<Controller> findAnyMatching(Recipient recipient, Optional<String> optionalFilter, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return controllerRepository.findByNameContainingIgnoreCase(recipient, optionalFilter.get(), pageable);
        } else {
            return controllerRepository.findAll(recipient, pageable);
        }
    }

    public long countAnyMatching(Recipient recipient, Optional<String> optionalFilter) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return controllerRepository.countByNameContainingIgnoreCase(recipient, optionalFilter.get());
        } else {
            return controllerRepository.count(recipient);
        }
    }

    public Page<Controller> find(Pageable pageable) {
        return controllerRepository.findAll(pageable);
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
        e.setSensors(new ArrayList<>());
        return e;
    }

    @Override
    public Controller save(User currentUser, Controller entity) {
        try {
            return CrudService.super.save(currentUser, entity);
        } catch (DataIntegrityViolationException e) {
            throw new UserFriendlyDataException(
                    "There is already a controller with that name. Please select a unique name for the controller.");
        }

    }
}