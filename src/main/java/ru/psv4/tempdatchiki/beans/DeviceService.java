package ru.psv4.tempdatchiki.beans;

import org.springframework.stereotype.Service;
import ru.psv4.tempdatchiki.model.Device;

@Service
public class DeviceService extends ReferenceService<Device> {
    public DeviceService() {
        super(Device.class);
    }
}
