package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SensorFieldInitValues {

    String controllerUid;
    String backwardUrl;
    String name;
    Double minValue;
    Double maxValue;

    public void setBackwardUrl(String backwardUrl) {
        this.backwardUrl = backwardUrl;
    }

    public void setControllerUid(String controllerUid) {
        this.controllerUid = controllerUid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public static SensorFieldInitValues parse(QueryParameters queryParameters) {
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        SensorFieldInitValues values = new SensorFieldInitValues();
        values.name = parametersMap.get("name").get(0);
        values.backwardUrl = parametersMap.get("backwardUrl").get(0);
        values.controllerUid = parametersMap.get("controllerUid").get(0);
        values.minValue = Double.parseDouble(parametersMap.get("minValue").get(0));
        values.maxValue = Double.parseDouble(parametersMap.get("maxValue").get(0));
        return values;
    }

    public static QueryParameters convert(SensorFieldInitValues values) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", values.name);
        map.put("backwardUrl", values.backwardUrl);
        map.put("controllerUid", values.controllerUid);
        map.put("minValue", Double.toString(values.minValue));
        map.put("maxValue", Double.toString(values.maxValue));
        return QueryParameters.simple(map);
    }
}
