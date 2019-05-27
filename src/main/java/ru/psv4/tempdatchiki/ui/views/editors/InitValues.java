package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InitValues {
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

    public static InitValues parse(QueryParameters queryParameters) throws Exception {
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        InitValues values = new InitValues();
        values.uid = parametersMap.get("uid").get(0);
        values.backwardUrl = parametersMap.get("backwardUrl").get(0);
        values.entityClass = parametersMap.get("entityClass").get(0);
        values.property = parametersMap.get("property").get(0);
        values.value = parametersMap.get("value").get(0);
        return values;
    }

    public static QueryParameters convert(InitValues values) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("uid", values.uid);
        map.put("backwardUrl", values.backwardUrl);
        map.put("entityClass", values.entityClass);
        map.put("property", values.property);
        map.put("value", values.value);
        return QueryParameters.simple(map);
    }
}
