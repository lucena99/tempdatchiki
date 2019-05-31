package ru.psv4.tempdatchiki.backend.data;

public enum EventType {

    OverDown(1), OverUp(2), Error(3), Normal(4);

    private int code;

    EventType(int code) { this.code = code; }

    public int getCode() {
        return code;
    }

    public static EventType getByCode(int code) {
        switch (code) {
            case 1: return OverDown;
            case 2: return OverUp;
            case 3: return Error;
            case 4: return Normal;
            default: throw new IllegalArgumentException(String.format("Unknown event type code %d", code));
        }
    }
}