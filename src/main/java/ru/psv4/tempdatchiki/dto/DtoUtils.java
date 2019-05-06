package ru.psv4.tempdatchiki.dto;

import ru.psv4.tempdatchiki.model.Reference;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DtoUtils {

    public static <R extends Reference, D extends ReferenceDto> List<D> convert(Class<D> dClass, List<R> list) {
        List<D> result = new ArrayList<D>();
        for (R r : list) {
            result.add(convert(dClass, r));
        }
        return result;
    }

    public static <R extends Reference, D extends ReferenceDto> D convert(Class<D> dClass, R r) {
        try {
            D dto = dClass.newInstance();
            Class<R> rClass = (Class<R>)dClass.getAnnotation(EntityClass.class).value();
            for (Field f : getFields(dClass)) {
                Class<EntityField> ef = EntityField.class;
                if (f.isAnnotationPresent(ef)) {
                    f.setAccessible(true);
                    String[] properties = f.getAnnotation(ef).value().split("\\.");
                    Class<?> oClass = rClass;
                    Object pValue = r;
                    for (String property : properties) {
                        Field pField = getField(oClass, property);
                        pField.setAccessible(true);
                        pValue = pField.get(pValue);
                        oClass = pField.getDeclaringClass();
                    }
                    f.set(dto, pValue);
                }
            }
            return dto;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<Field> getFields(Class<T> tClass) {
        List<Field> fields = new ArrayList<>();
        Class clazz = tClass;
        while (clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static <T> Field getField(Class<T> tClass, String name) {
        return getFields(tClass).stream().filter(e -> e.getName().equals(name)).findFirst().get();
    }
}
