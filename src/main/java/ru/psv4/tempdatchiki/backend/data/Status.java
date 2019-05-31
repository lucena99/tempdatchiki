package ru.psv4.tempdatchiki.backend.data;

public enum Status {

    Normal(0), Error(1), Absence(2);

    private int code;

    Status(int code) { this.code = code; }

    public int getCode() {
        return code;
    }

    public static Status getByCode(int code) {
        switch (code) {
            case 0: return Normal;
            case 1: return Error;
            case 2: return Absence;
            default: throw new IllegalArgumentException(String.format("Unknown status code %d", code));
        }
    }
}