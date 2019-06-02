package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.SensorRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorService extends ReferenceService<Sensor> implements CrudService<Sensor> {

    @Autowired
    private SensorRepository sensorRepository;

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

    @Override
    public SensorRepository getRepository() {
        return sensorRepository;
    }

    public List<Sensor> getListOrderByControllerNameAndNum() {
        return sensorRepository.findAll(Sort.by("controller.name", "num").ascending());
    }

    @Transactional
    @Override
    public Sensor createNew(User currentUser) {
        Sensor e = new Sensor();
        e.setUid(UIDUtils.generate());
        e.setCreatedDatetime(LocalDateTime.now());
        return e;
    }
}
