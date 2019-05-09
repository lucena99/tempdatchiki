package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Recipient;
import ru.psv4.tempdatchiki.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends ReferenceService<User> {

    public UserService() {
        super(User.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<User> findByLoginIgnoreCase(String login) {
        return em.createQuery("SELECT u FROM " + eClass.getName() + " u " +
                "WHERE LOWER(u.login) = LOWER(:login)", eClass)
                .setParameter("login", login)
                .getResultList().stream().findFirst();
    }
}
