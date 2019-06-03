package ru.psv4.tempdatchiki.backend.restapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import ru.psv4.tempdatchiki.backend.data.Incident;

import java.time.LocalDateTime;

@EntityClass(Incident.class)
public class IncidentDto {

    @JsonProperty("uid")
    @EntityField("uid")
    private String uid;

    @JsonProperty("sensorUid")
    @EntityField("sensor.uid")
    private String sensorUid;

    @JsonProperty("controllerName")
    @EntityField("sensor.controller.name")
    private String controllerName;

    @JsonProperty("sensorName")
    @EntityField("sensor.name")
    private String sensorName;

    @JsonProperty("minValue")
    @EntityField("minValue")
    private Double minValue;

    @JsonProperty("maxValue")
    @EntityField("maxValue")
    private Double maxValue;

    @JsonProperty("value")
    @EntityField("value")
    private Double value;

    @JsonProperty("typeCode")
    @EntityField("type.code")
    private Integer typeCode;

    @JsonProperty("typeName")
    @EntityField("type.name")
    private String typeName;

    @EntityField("createdDatetime")
    @ApiModelProperty(notes = "Дата-время инцидента")
    @JsonProperty("created_datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDatetime;

}
