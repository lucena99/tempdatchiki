package ru.psv4.tempdatchiki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Status;

import java.time.LocalDateTime;

@EntityClass(Sensor.class)
public class SensorDto extends ReferenceDto {
    @JsonProperty("controllerUid")
    @EntityField("controller.uid")
    private String controllerUid;

    @JsonProperty("minValue")
    @EntityField("minValue")
    private Double minValue;

    @JsonProperty("maxValue")
    @EntityField("maxValue")
    private Double maxValue;

    @JsonProperty("value")
    @EntityField("temp.value")
    private Double value;

    @JsonProperty("statusCode")
    @EntityField("temp.status.code")
    private Integer statusCode;

    @JsonProperty("statusName")
    @EntityField("temp.status.name")
    private String statusName;

    @EntityField("temp.updatedDatetime")
    @ApiModelProperty(notes = "Дата-время обновления")
    @JsonProperty("updated_datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDatetime;

    public String getControllerUid() {
        return controllerUid;
    }

    public void setControllerUid(String controllerUid) {
        this.controllerUid = controllerUid;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public LocalDateTime getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(LocalDateTime updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }
}
