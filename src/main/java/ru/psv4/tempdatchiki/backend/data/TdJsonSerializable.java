package ru.psv4.tempdatchiki.backend.data;

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;
import ru.psv4.tempdatchiki.ui.vaadin_json.JsonSerializerUtils;

public interface TdJsonSerializable extends JsonSerializable {
    @Override
    public default JsonObject toJson() {
        return JsonSerializerUtils.toJson(this);
    }

    @Override
    public default JsonSerializable readJson(JsonObject value) {
        return JsonSerializerUtils.readJson(value);
    }
}
