package ru.psv4.tempdatchiki.backend.data;

public enum Tag {

    OverDown(1), OverUp(2), Error(3), Normal(4);

    private int code;

    private Tag(int code) { this.code = code; }

    public static Tag getByCode(int code) {
        switch (code) {
            case 1: return OverDown;
            case 2: return OverUp;
            case 3: return Error;
            case 4: return Normal;
            default: throw new IllegalArgumentException(String.format("Unknown event type code %d", code));
        }
    }
}
