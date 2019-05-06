package ru.psv4.tempdatchiki.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({"controllerUid", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorCreateDto {
    @ApiModelProperty(required = true)
    @JsonProperty("controllerUid")
    private String controllerUid;

    @ApiModelProperty(required = true)
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getControllerUid() { return controllerUid; }

    public void setControllerUid(String controllerUid) { this.controllerUid = controllerUid; }
}
