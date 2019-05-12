package ru.psv4.tempdatchiki.backend.data;

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;
import ru.psv4.tempdatchiki.vaadin_json.JsonSerializerUtils;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedEntityGraphs({@NamedEntityGraph(name = Controller.ENTITY_GRAPTH_FULL, attributeNodes = {
        @NamedAttributeNode("sensors")
})})
public class Controller extends Reference implements JsonSerializable {

    public static final String ENTITY_GRAPTH_FULL = "Controller.full";

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "controller")
    private List<Sensor> sensors;

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    @Override
    public JsonObject toJson() {
        return JsonSerializerUtils.toJson(this);
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        return JsonSerializerUtils.readJson(value);
    }
}
