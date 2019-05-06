package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.model.Controller;
import ru.psv4.tempdatchiki.model.Sensor;

import java.util.List;

@Service
public class SensorService extends ReferenceService<Sensor> {
    public SensorService() {
        super(Sensor.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Sensor> getByNameIgnoreCase(String name, Controller c) {
        return em.createQuery("SELECT s FROM Sensor s " +
                "WHERE LOWER(s.name) = LOWER(:name) AND s.controller = :controller ORDER BY s.createdDatetime", eClass)
                .setParameter("name", name)
                .setParameter("controller", c)
                .getResultList();
    }
}
