package ru.psv4.tempdatchiki.backend.data;

public enum EventType {

    OverDown(1), OverUp(2), Error(3);

    private int code;

    private EventType(int code) { this.code = code; }

    public static EventType getByCode(int code) {
        switch (code) {
            case 1: return OverDown;
            case 2: return OverUp;
            case 3: return Error;
            default: throw new IllegalArgumentException(String.format("Unknown event type code %d", code));
        }
    }
}
