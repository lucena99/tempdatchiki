package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import ru.psv4.tempdatchiki.model.Sensor;

@Service
public class SensorService extends ReferenceService<Sensor> {
    public SensorService() {
        super(Sensor.class);
    }
}
