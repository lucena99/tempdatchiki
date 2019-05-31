package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Temp;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.TempRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

@Service
public class TempService implements CrudService<Temp> {

    @Autowired
    private TempRepository tempRepository;

    @Override
    public TempRepository getRepository() {
        return tempRepository;
    }

    @Transactional
    @Override
    public Temp createNew(User currentUser) {
        Temp e = new Temp();
        e.setUid(UIDUtils.generate());
        e.setUpdatedDatetime(LocalDateTime.now());
        return e;
    }
}