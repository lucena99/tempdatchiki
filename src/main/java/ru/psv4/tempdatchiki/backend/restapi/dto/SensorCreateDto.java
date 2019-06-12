package ru.psv4.tempdatchiki.backend.restapi.dto;

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

    @ApiModelProperty(required = true)
    @JsonProperty("num")
    private int num;

    @ApiModelProperty(required = true)
    @JsonProperty("minValue")
    private double minValue;

    @ApiModelProperty(required = true)
    @JsonProperty("maxValue")
    private double maxValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getControllerUid() {
        return controllerUid;
    }

    public void setControllerUid(String controllerUid) {
        this.controllerUid = controllerUid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
