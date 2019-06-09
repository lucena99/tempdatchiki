package ru.psv4.tempdatchiki.backend.data;

import ru.psv4.tempdatchiki.backend.schedulers.TempEvent;

public enum IncidentType {

    OutDown(1, "Ниже границы"), OutUp(2, "Выше границы"),
    Error(3, "Ошибка"), Normal(4, "Норма");

    private int code;
    private String name;

    IncidentType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static IncidentType getByCode(int code) {
        switch (code) {
            case 1: return OutDown;
            case 2: return OutUp;
            case 3: return Error;
            case 4: return Normal;
            default: throw new IllegalArgumentException(String.format("Unknown incident type code %d", code));
        }
    }

    public static IncidentType defineType(TempEvent event) {
        Status status = event.getStatusNew();
        Sensor sensor = event.getSensor();
        double value = event.getValueNew();
        switch (event.getStatusNew()) {
            case Error:
            case Off:
            case Unreachable:
            case NotFound:
            case SystemError:
                return IncidentType.Error;
            case On:
                if (value < sensor.getMinValue()) {
                    return IncidentType.OutDown;
                } else if (value > sensor.getMaxValue()) {
                    return IncidentType.OutUp;
                } else {
                    return IncidentType.Normal;
                }
            default:
                throw new IllegalStateException("Can't define incident type");
        }
    }
}