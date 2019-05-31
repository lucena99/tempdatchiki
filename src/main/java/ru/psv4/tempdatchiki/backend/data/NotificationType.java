package ru.psv4.tempdatchiki.backend.data;

public enum NotificationType {

    OverDown(1), OverUp(2), Error(3), Normal(4);

    private int code;

    NotificationType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static NotificationType getByCode(int code) {
        switch (code) {
            case 1: return OverDown;
            case 2: return OverUp;
            case 3: return Error;
            case 4: return Normal;
            default: throw new IllegalArgumentException(String.format("Unknown event type code %d", code));
        }
    }
}