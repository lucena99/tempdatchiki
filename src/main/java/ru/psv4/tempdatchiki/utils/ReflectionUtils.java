package ru.psv4.tempdatchiki.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReflectionUtils {

    public static <T> List<Field> getFields(Class<T> tClass) {
        List<Field> fields = new ArrayList<>();
        Class clazz = tClass;
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static <T> Optional<Field> getField(Class<T> tClass, String name) {
        return getFields(tClass).stream().filter(e -> e.getName().equals(name)).findFirst();
    }
}
