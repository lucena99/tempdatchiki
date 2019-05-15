package ru.psv4.tempdatchiki.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class Lazy<T> {

    private Supplier<T> creator;

    private T obj;

    public Lazy(Supplier<T> creator) {
        this.creator = creator;
    }

    public T get() {
        if (obj == null) {
            obj = creator.get();
        }
        return obj;
    }

    public Optional<T> getOptional() {
        return Optional.ofNullable(obj);
    }
}
