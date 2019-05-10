package ru.psv4.tempdatchiki.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({"uid", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipientCreateDto {
    @ApiModelProperty(required = true)
    @JsonProperty("uid")
    private String uid;

    @ApiModelProperty(required = true)
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
}