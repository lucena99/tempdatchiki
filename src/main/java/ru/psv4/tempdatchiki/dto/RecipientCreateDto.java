package ru.psv4.tempdatchiki.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({"fcmToken", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipientCreateDto {

    @ApiModelProperty(required = true)
    @JsonProperty("fcmToken")
    private String fcmToken;

    @ApiModelProperty(required = true)
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
