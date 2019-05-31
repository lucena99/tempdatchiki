package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Notification;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.NotificationRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

@Service
public class NotificationService implements CrudService<Notification> {

    @Autowired
    private NotificationRepository messageRepository;

    @Override
    public NotificationRepository getRepository() {
        return messageRepository;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Notification createNew(User currentUser) {
        Notification e = new Notification();
        e.setUid(UIDUtils.generate());
        e.setCreatedDatetime(LocalDateTime.now());
        return e;
    }
}