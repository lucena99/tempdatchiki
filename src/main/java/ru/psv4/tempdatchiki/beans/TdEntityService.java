package ru.psv4.tempdatchiki.beans;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.TdEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

public class TdEntityService<E extends TdEntity> {

    protected Class<E> eClass;

    @PersistenceContext
    protected EntityManager em;

    public TdEntityService(Class<E> refClass) {
        this.eClass = refClass;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public E getByUid(String uid) throws NotFoundException {
        try {
            return em.createQuery("SELECT r FROM " + eClass.getName() + " r WHERE r.uid = :uid", eClass)
                    .setParameter("uid", uid)
                    .getSingleResult();
        } catch (EntityNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByUid(String uid) throws NotFoundException {
        em.remove(getByUid(uid));
    }
}
