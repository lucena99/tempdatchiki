package ru.psv4.tempdatchiki.backend.data;

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;
import ru.psv4.tempdatchiki.vaadin_json.JsonSerializerUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@NamedEntityGraphs({@NamedEntityGraph(name = Controller.ENTITY_GRAPTH_FULL, attributeNodes = {
        @NamedAttributeNode("sensors")
})})
public class Controller extends Reference implements TdJsonSerializable {

    public static final String ENTITY_GRAPTH_FULL = "Controller.full";

    @NotNull
    private String url;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "controller")
    @OrderBy("num ASC")
    private List<Sensor> sensors;

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}
