package ru.psv4.tempdatchiki.beans;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Device;
import ru.psv4.tempdatchiki.model.Reference;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public abstract class ReferenceService<R extends Reference> {

    @PersistenceContext
    private EntityManager em;

    private Class<R> refClass;

    public ReferenceService(Class<R> refClass) {
        this.refClass = refClass;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public R save(R ref) {
        return em.merge(ref);
    }

    public List<R> getList() {
        return em.createQuery("SELECT r FROM " + refClass.getName() + " r", refClass).getResultList();
    }
}
