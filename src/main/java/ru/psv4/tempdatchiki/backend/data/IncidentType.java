package ru.psv4.tempdatchiki.backend.data;

import ru.psv4.tempdatchiki.backend.schedulers.TempEvent;

public enum IncidentType {

    OverDown(1), OverUp(2), Error(3), Normal(4);

    private int code;

    IncidentType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static IncidentType getByCode(int code) {
        switch (code) {
            case 1: return OverDown;
            case 2: return OverUp;
            case 3: return Error;
            case 4: return Normal;
            default: throw new IllegalArgumentException(String.format("Unknown event type code %d", code));
        }
    }

    public static IncidentType defineType(TempEvent event) {
        Status status = event.getStatusNew();
        Sensor sensor = event.getSensor();
        double value = event.getValueNew();
        switch (event.getStatusNew()) {
            case Error:
            case Absence:
                return IncidentType.Error;
            case Normal:
                if (value < sensor.getMinValue()) {
                    return IncidentType.OverDown;
                } else if (value > sensor.getMaxValue()) {
                    return IncidentType.OverUp;
                } else {
                    return IncidentType.Normal;
                }
            default:
                throw new IllegalStateException("Can't define incident type");
        }
    }
}