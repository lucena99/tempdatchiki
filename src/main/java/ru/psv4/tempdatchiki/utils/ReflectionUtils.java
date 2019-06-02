package ru.psv4.tempdatchiki.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public static <T> List<Method> getMethods(Class<T> tClass) {
        List<Method> methods = new ArrayList<>();
        Class clazz = tClass;
        while (clazz != Object.class) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    public static <T> Optional<Field> getField(Class<T> tClass, String name) {
        Class clazz = tClass;
        while (clazz != Object.class) {
            Optional<Field> opField = Arrays.stream(clazz.getDeclaredFields()).filter(e -> e.getName().equals(name)).findFirst();
            if (opField.isPresent()) {
                return opField;
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<Method> getMethod(Class<T> tClass, String name) {
        Class clazz = tClass;
        while (clazz != Object.class) {
            Optional<Method> opMethod = Arrays.stream(clazz.getDeclaredMethods()).filter(e -> e.getName().equals(name)).findFirst();
            if (opMethod.isPresent()) {
                return opMethod;
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return Optional.empty();
    }
}
