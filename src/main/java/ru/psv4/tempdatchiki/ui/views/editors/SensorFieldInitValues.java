package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

public class SensorFieldInitValues {

    String controllerUid;
    String backwardUrl;
    String name;
    Double minValue;
    Double maxValue;
    boolean aNew;
    Integer num;

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

    public void setaNew(boolean aNew) {
        this.aNew = aNew;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public static SensorFieldInitValues parse(QueryParameters queryParameters) {
        ParametersParser params = new ParametersParser(queryParameters);
        SensorFieldInitValues values = new SensorFieldInitValues();
        values.name = params.asString("name");
        values.backwardUrl = params.asString("backwardUrl");
        values.controllerUid = params.asString("controllerUid");
        values.minValue = params.asDouble("minValue");
        values.maxValue = params.asDouble("maxValue");
        values.aNew = params.asBooleanSimple("aNew");
        values.num = params.asInteger("num");
        return values;
    }

    public static QueryParameters convert(SensorFieldInitValues values) {
        return new ParametersBuilder()
                .putString("name", values.name)
                .putString("backwardUrl", values.backwardUrl)
                .putString("controllerUid", values.controllerUid)
                .putDouble("minValue", values.minValue)
                .putDouble("maxValue", values.maxValue)
                .putBoolean("aNew", values.aNew)
                .putInteger("num", values.num)
                .build();
    }
}