package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.RecipientRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipientService extends ReferenceService<Recipient> implements CrudService<Recipient> {

    @Autowired
    private RecipientRepository recipientRepository;

    public RecipientService() {
        super(Recipient.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Controller> getControllersByRecipientUid(Recipient recipient) {
        return em.createQuery("SELECT e.controller FROM Subscription e " +
                "WHERE e.recipient = :recipient ORDER BY e.subscribedDatetime", Controller.class)
                .setParameter("recipient", recipient).getResultList();
    }

    @Override
    public JpaRepository<Recipient, String> getRepository() {
        return recipientRepository;
    }

    public Page<Recipient> findAnyMatching(Optional<String> optionalFilter, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return recipientRepository.findByNameContainingIgnoreCase(optionalFilter.get(), pageable);
        } else {
            return recipientRepository.findAll(pageable);
        }
    }

    public long countAnyMatching(Optional<String> optionalFilter) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return recipientRepository.countByNameContainingIgnoreCase(optionalFilter.get());
        } else {
            return recipientRepository.count();
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Recipient createNew(User currentUser) {
        Recipient e = new Recipient();
        e.setUid(UIDUtils.generate());
        e.setCreatedDatetime(LocalDateTime.now());
        e.setSubscriptions(new ArrayList<Subscription>());
        return e;
    }
}