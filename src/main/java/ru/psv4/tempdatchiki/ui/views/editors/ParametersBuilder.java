package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.router.QueryParameters;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParametersBuilder {

    private Map<String, String> map = new LinkedHashMap<>();

    public QueryParameters build() {
        return QueryParameters.simple(map);
    }

    public ParametersBuilder putString(String key, String v) {
        if (v != null) {
            map.put(key, v);
        }
        return this;
    }

    public ParametersBuilder putBoolean(String key, Boolean v) {
        if (v != null) {
            map.put(key, String.valueOf(v));
        }
        return this;
    }

    public ParametersBuilder putDouble(String key, Double v) {
        if (v != null) {
            map.put(key, v.toString());
        }
        return this;
    }

    public ParametersBuilder putInteger(String key, Integer v) {
        if (v != null) {
            map.put(key, v.toString());
        }
        return this;
    }
}
