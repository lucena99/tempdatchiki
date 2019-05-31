package ru.psv4.tempdatchiki.backend.schedulers;

import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.NotificationType;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AnalyzedTemp {
    NotificationType state;
    Sensor sensor;
    float value;

    private static DecimalFormat tempFormatter;
    static {
        tempFormatter = new DecimalFormat("#.#");
        DecimalFormatSymbols sep = new DecimalFormatSymbols(Locale.getDefault());
        sep.setDecimalSeparator('.');
        tempFormatter.setDecimalFormatSymbols(sep);
    }

    AnalyzedTemp(NotificationType tag, Sensor sensor, float value) {
        this.state = tag;
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
}
