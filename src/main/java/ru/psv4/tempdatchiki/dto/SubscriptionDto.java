package ru.psv4.tempdatchiki.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({"controllerUid", "recipientUid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionDto {

    @ApiModelProperty(required = true)
    @JsonProperty("recipientUid")
    private String recipientUid;

    @ApiModelProperty(required = true)
    @JsonProperty("controllerUid")
    private String controllerUid;

    public String getRecipientUid() { return recipientUid; }

    public void setRecipientUid(String recipientUid) { this.recipientUid = recipientUid; }

    public String getControllerUid() { return controllerUid; }

    public void setControllerUid(String controllerUid) { this.controllerUid = controllerUid; }
}
