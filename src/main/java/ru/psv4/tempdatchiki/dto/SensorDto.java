package ru.psv4.tempdatchiki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.psv4.tempdatchiki.backend.data.Sensor;

@EntityClass(Sensor.class)
public class SensorDto extends ReferenceDto {
    @JsonProperty("controllerUid")
    @EntityField("controller.uid")
    private String controllerUid;

    public String getControllerUid() { return controllerUid; }

    public void setControllerUid(String controllerUid) { this.controllerUid = controllerUid; }
}
