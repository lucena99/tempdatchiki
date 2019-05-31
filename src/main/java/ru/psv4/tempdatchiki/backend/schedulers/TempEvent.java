package ru.psv4.tempdatchiki.backend.schedulers;

import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.EventType;
import ru.psv4.tempdatchiki.backend.data.Status;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TempEvent {

    private EventType state;
    private Sensor sensor;
    private double value;

    private static DecimalFormat tempFormatter;
    static {
        tempFormatter = new DecimalFormat("#.#");
        DecimalFormatSymbols sep = new DecimalFormatSymbols(Locale.getDefault());
        sep.setDecimalSeparator('.');
        tempFormatter.setDecimalFormatSymbols(sep);
    }

    public TempEvent(Sensor sensor, Status stateOld, double valueOld, Status stateNew, double valueNew) {
        this.state = state;
        this.sensor = sensor;
        this.value = value;
    }

    @Override
    public String toString() {
        switch (state) {
            case OverDown:
                return String.format("%1$s.%2$s. %3$s(%4$s)",
                        sensor.getController().getName(), sensor.getName(),
                        tempFormatter.format(value), tempFormatter.format(sensor.getMinValue()));
            case OverUp:
                return String.format("%1$s.%2$s. %3$s(%4$s)",
                        sensor.getController().getName(), sensor.getName(),
                        tempFormatter.format(value), tempFormatter.format(sensor.getMaxValue()));
            default:
                return "Unknown";
        }
    }

    public EventType getState() {
        return state;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public double getValue() {
        return value;
    }
}
