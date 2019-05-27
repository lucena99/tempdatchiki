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
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        SubsriptionFieldInitValues values = new SubsriptionFieldInitValues();
        values.recipientUid = parametersMap.get("recipientUid").get(0);
        values.backwardUrl = parametersMap.get("backwardUrl").get(0);
        values.controllerUid = parametersMap.get("controllerUid").get(0);
        values.over = Boolean.parseBoolean(parametersMap.get("over").get(0));
        values.error = Boolean.parseBoolean(parametersMap.get("error").get(0));
        return values;
    }

    public static QueryParameters convert(SubsriptionFieldInitValues values) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("recipientUid", values.recipientUid);
        map.put("backwardUrl", values.backwardUrl);
        map.put("controllerUid", values.controllerUid);
        map.put("over", Boolean.toString(values.over));
        map.put("error", Boolean.toString(values.error));
        return QueryParameters.simple(map);
    }
}
