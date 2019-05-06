package ru.psv4.tempdatchiki.beans;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Reference;

import java.util.List;

public abstract class ReferenceService<R extends Reference> extends TdEntityService<R> {

    public ReferenceService(Class<R> refClass) {
        super(refClass);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public R save(R ref) {
        return em.merge(ref);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<R> getList() {
        return em.createQuery("SELECT r FROM " + eClass.getName() + " r ORDER BY r.createdDatetime", eClass).getResultList();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<R> getByNameIgnoreCase(String name) {
        return em.createQuery("SELECT r FROM " + eClass.getName() + " r " +
                "WHERE LOWER(r.name) = LOWER(:name) ORDER BY r.createdDatetime", eClass)
                .setParameter("name", name)
                .getResultList();
    }
}
