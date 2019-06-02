package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SubsriptionFieldInitValues {

    String recipientUid;
    String backwardUrl;
    String controllerUid;
    Boolean over;
    Boolean error;

    public void setRecipientUid(String recipientUid) {
        this.recipientUid = recipientUid;
    }

    public void setBackwardUrl(String backwardUrl) {
        this.backwardUrl = backwardUrl;
    }

    public void setControllerUid(String controllerUid) {
        this.controllerUid = controllerUid;
    }

    public void setOver(Boolean over) {
        this.over = over;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public static SubsriptionFieldInitValues parse(QueryParameters queryParameters) {
        Map<String, List<String>> map = queryParameters.getParameters();
        SubsriptionFieldInitValues values = new SubsriptionFieldInitValues();
        values.recipientUid = map.get("recipientUid").get(0);
        values.backwardUrl = map.get("backwardUrl").get(0);
        values.controllerUid = map.get("controllerUid").get(0);
        values.over = map.containsKey("over") ? Boolean.parseBoolean(map.get("over").get(0)) : null;
        values.error = map.containsKey("error") ? Boolean.parseBoolean(map.get("error").get(0)) : null;
        return values;
    }

    public static QueryParameters convert(SubsriptionFieldInitValues values) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("recipientUid", values.recipientUid);
        map.put("backwardUrl", values.backwardUrl);
        map.put("controllerUid", values.controllerUid);
        map.put("over", values.over != null ? Boolean.toString(values.over) : "");
        map.put("error", values.error != null ? Boolean.toString(values.error) : "");
        return QueryParameters.simple(map);
    }
}
