package ru.psv4.tempdatchiki.backend.data;

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.JsonObject;
import ru.psv4.tempdatchiki.vaadin_json.JsonSerializerUtils;
import ru.psv4.tempdatchiki.vaadin_json.TdJsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Sensor extends Reference implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "controller_uid", nullable = false)
    @TdJsonIgnore
    private Controller controller;

    private int num;

    @Column(name = "minvalue")
    private double minValue;

    @Column(name = "maxvalue")
    private double maxValue;

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public double getMinValue() { return minValue; }

    public void setMinValue(double minValue) { this.minValue = minValue; }

    public double getMaxValue() { return maxValue; }

    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }

    public int getNum() { return num; }

    public void setNum(int num) { this.num = num; }

    @Override
    public JsonObject toJson() { return JsonSerializerUtils.toJson(this); }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        return JsonSerializerUtils.readJson(value);
    }
}
