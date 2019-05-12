package ru.psv4.tempdatchiki.backend.data;

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;
import ru.psv4.tempdatchiki.vaadin_json.JsonSerializerUtils;
import ru.psv4.tempdatchiki.vaadin_json.TdJsonIgnore;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Sensor extends Reference implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "controller_uid", nullable = false)
    @TdJsonIgnore
    private Controller controller;

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public JsonObject toJson() { return JsonSerializerUtils.toJson(this); }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        return JsonSerializerUtils.readJson(value);
    }
}
