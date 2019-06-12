package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

import java.util.List;
import java.util.Map;

public class ParametersParser {

    private Map<String, List<String>> map;

    public ParametersParser(Map<String, List<String>> map) {
        this.map = map;
    }

    public ParametersParser(QueryParameters queryParameters) {
        this(queryParameters.getParameters());
    }

    public Double asDouble(String key) {
        return map.containsKey(key) ? Double.parseDouble(map.get(key).get(0)) : null;
    }

    public String asString(String key) {
        return map.containsKey(key) ? map.get(key).get(0) : null;
    }

    public boolean asBooleanSimple(String key) {
        return map.containsKey(key) ? Boolean.parseBoolean(map.get(key).get(0)) : false;
    }

    public Integer asInteger(String key) {
        return map.containsKey(key) ? Integer.parseInt(map.get(key).get(0)) : null;
    }
}
