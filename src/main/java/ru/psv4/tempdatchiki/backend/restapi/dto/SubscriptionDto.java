package ru.psv4.tempdatchiki.backend.restapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import ru.psv4.tempdatchiki.backend.data.Subscription;

import java.time.LocalDateTime;

@EntityClass(Subscription.class)
@JsonPropertyOrder({"recipientUid", "controllerUid"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionDto {

    @EntityField("recipient.uid")
    @ApiModelProperty(required = true)
    @JsonProperty("recipientUid")
    private String recipientUid;

    @EntityField("controller.uid")
    @ApiModelProperty(required = true)
    @JsonProperty("controllerUid")
    private String controllerUid;

    @EntityField("createdDatetime")
    @ApiModelProperty(notes = "Дата-время создания")
    @JsonProperty("created_datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDatetime;

    @EntityField("notifyOver")
    @ApiModelProperty(required = true)
    @JsonProperty("notifyOver")
    private boolean notifyOver;

    @EntityField("notifyError")
    @ApiModelProperty(required = true)
    @JsonProperty("notifyError")
    private boolean notifyError;

    public String getRecipientUid() { return recipientUid; }

    public void setRecipientUid(String recipientUid) { this.recipientUid = recipientUid; }

    public String getControllerUid() { return controllerUid; }

    public void setControllerUid(String controllerUid) { this.controllerUid = controllerUid; }

    public LocalDateTime getCreatedDatetime() { return createdDatetime; }

    public void setCreatedDatetime(LocalDateTime createdDatetime) { this.createdDatetime = createdDatetime; }

    public boolean isNotifyOver() {
        return notifyOver;
    }

    public void setNotifyOver(boolean notifyOver) {
        this.notifyOver = notifyOver;
    }

    public boolean isNotifyError() {
        return notifyError;
    }

    public void setNotifyError(boolean notifyError) {
        this.notifyError = notifyError;
    }
}
