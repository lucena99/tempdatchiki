package ru.psv4.tempdatchiki.backend.restapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;

@JsonPropertyOrder({"recipientUid", "controllerUid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionCreateDto {

    @ApiModelProperty(required = true)
    @JsonProperty("recipientUid")
    private String recipientUid;

    @EntityField("controller.uid")
    @ApiModelProperty(required = true)
    @JsonProperty("controllerUid")
    private String controllerUid;

    @EntityField("notifyOut")
    @ApiModelProperty(required = true)
    @JsonProperty("notifyOut")
    private boolean notifyOut;

    @EntityField("notifyError")
    @ApiModelProperty(required = true)
    @JsonProperty("notifyError")
    private boolean notifyError;

    public String getRecipientUid() { return recipientUid; }

    public void setRecipientUid(String recipientUid) { this.recipientUid = recipientUid; }

    public String getControllerUid() { return controllerUid; }

    public void setControllerUid(String controllerUid) { this.controllerUid = controllerUid; }

    public boolean isNotifyOut() {
        return notifyOut;
    }

    public void setNotifyOut(boolean notifyOut) {
        this.notifyOut = notifyOut;
    }

    public boolean isNotifyError() {
        return notifyError;
    }

    public void setNotifyError(boolean notifyError) {
        this.notifyError = notifyError;
    }
}
