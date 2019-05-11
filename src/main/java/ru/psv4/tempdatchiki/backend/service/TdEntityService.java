package ru.psv4.tempdatchiki.backend.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.TdEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public abstract class TdEntityService<E extends TdEntity> {

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
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByUid(String uid) throws NotFoundException {
        em.remove(getByUid(uid));
    }
}
