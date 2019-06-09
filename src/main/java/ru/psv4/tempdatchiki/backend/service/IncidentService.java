package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.*;
import ru.psv4.tempdatchiki.backend.repositories.IncidentRepository;
import ru.psv4.tempdatchiki.backend.repositories.TempRepository;
import ru.psv4.tempdatchiki.backend.schedulers.ControllerEvent;
import ru.psv4.tempdatchiki.backend.schedulers.TempEvent;
import ru.psv4.tempdatchiki.utils.IncidentDecisionResolver;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IncidentService implements CrudService<Incident>, InitializingBean, EventBroker.EventListener {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private EventBroker eventBroker;

    @Override
    public IncidentRepository getRepository() {
        return incidentRepository;
    }

    @Transactional
    @Override
    public Incident createNew(User currentUser) {
        Incident e = new Incident();
        e.setUid(UIDUtils.generate());
        e.setCreatedDatetime(LocalDateTime.now());
        return e;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBroker.addListener(this);
    }

    @PreDestroy
    public void destroy() {
        eventBroker.removeListener(this);
    }

    @Override
    public void onEvent(ControllerEvent controllerEvent) {
        for (TempEvent event : controllerEvent.getTempEvents()) {
            IncidentDecisionResolver.resolve(event,
                    (e) -> {
                        return getRepository().findBySensorLast(event.getSensor());
                    },
                    (e, it) -> {
                        Sensor sensor = event.getSensor();
                        Incident incident = new Incident();
                        incident.setUid(UIDUtils.generate());
                        incident.setCreatedDatetime(LocalDateTime.now());
                        incident.setSensor(sensor);
                        incident.setValue(event.getValueNew());
                        incident.setMinValue(sensor.getMinValue());
                        incident.setMaxValue(sensor.getMaxValue());
                        incident.setType(it);
                        getRepository().saveAndFlush(incident);
                    });
        }
    }

    public Page<Incident> findAnyMatching(Optional<String> optionalFilter, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return incidentRepository.findByNameContainingIgnoreCase(optionalFilter.get(), pageable);
        } else {
            return incidentRepository.findAll(pageable);
        }
    }

    public long countAnyMatching(Optional<String> optionalFilter) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return incidentRepository.countByNameContainingIgnoreCase(optionalFilter.get());
        } else {
            return incidentRepository.count();
        }
    }
}