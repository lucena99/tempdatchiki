package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Message;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.MessageRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

@Service
public class MessageService implements CrudService<Message> {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public MessageRepository getRepository() {
        return messageRepository;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Message createNew(User currentUser) {
        Message e = new Message();
        e.setUid(UIDUtils.generate());
        e.setCreatedDatetime(LocalDateTime.now());
        return e;
    }
}