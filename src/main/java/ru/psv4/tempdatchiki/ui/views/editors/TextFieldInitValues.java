package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextFieldInitValues {
    String uid;
    String backwardUrl;
    String entityClass;
    String property;
    String value;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setBackwardUrl(String backwardUrl) {
        this.backwardUrl = backwardUrl;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String composeTitle() {
        return entityClass + ": " + property;
    }

    public static TextFieldInitValues parse(QueryParameters queryParameters) {
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        TextFieldInitValues values = new TextFieldInitValues();
        values.uid = parametersMap.get("uid").get(0);
        values.backwardUrl = parametersMap.get("backwardUrl").get(0);
        values.entityClass = parametersMap.get("entityClass").get(0);
        values.property = parametersMap.get("property").get(0);
        values.value = parametersMap.get("value").get(0);
        return values;
    }

    public static QueryParameters convert(TextFieldInitValues values) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uid", values.uid);
        map.put("backwardUrl", values.backwardUrl);
        map.put("entityClass", values.entityClass);
        map.put("property", values.property);
        map.put("value", values.value);
        return QueryParameters.simple(map);
    }
}
