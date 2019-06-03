package ru.psv4.tempdatchiki.backend.data;

public enum Status {

    Off(0, "Выключен"), On(1, "Включён"), Error(2, "Ошибка"),
    Unreachable(3, "Не отвечает"), SystemError(4, "System Error"), NotFound(5, "Not Found");

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
            case 3: return Unreachable;
            case 4: return SystemError;
            case 5: return NotFound;
            default: throw new IllegalArgumentException(String.format("Unknown status code %d", code));
        }
    }
}