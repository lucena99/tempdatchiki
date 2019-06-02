package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.Entity;

@Entity
public class Setting extends Reference {

    private String value;

    public static final String EVENT_HUB_AUTHORIZATION_KEY = "eventHubAuthorizationKey";
    public static final String EVENT_HUB_URL = "eventHubURL";
    public static final String DB_VERSION = "dbVersion";

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }
}
