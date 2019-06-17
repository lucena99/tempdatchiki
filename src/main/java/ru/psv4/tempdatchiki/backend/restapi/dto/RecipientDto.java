package ru.psv4.tempdatchiki.backend.restapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.psv4.tempdatchiki.backend.data.Recipient;

@EntityClass(Recipient.class)
public class RecipientDto extends ReferenceDto {
    @JsonProperty("fcmToken")
    @EntityField("fcmToken")
    private String fcmToken;
}
