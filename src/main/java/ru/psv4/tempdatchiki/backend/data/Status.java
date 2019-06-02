package ru.psv4.tempdatchiki.backend.data;

public enum Status {

    Off(0, "Выключен"), On(1, "Включён"), Error(2, "Ошибка");

    private int code;
    private String name;

    Status(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Status getByCode(int code) {
        switch (code) {
            case 0: return Off;
            case 1: return On;
            case 2: return Error;
            default: throw new IllegalArgumentException(String.format("Unknown status code %d", code));
        }
    }
}