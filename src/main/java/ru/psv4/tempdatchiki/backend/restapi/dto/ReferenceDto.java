package ru.psv4.tempdatchiki.backend.restapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@JsonPropertyOrder({"uid", "name", "created_datetime"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ReferenceDto {

    @JsonProperty("uid")
    @EntityField("uid")
    private String uid;

    @JsonProperty("name")
    @EntityField("name")
    private String name;

    @EntityField("createdDatetime")
    @ApiModelProperty(notes = "Дата-время создания")
    @JsonProperty("created_datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDatetime;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }
}
