package ru.psv4.tempdatchiki.vaadin_json;

import com.vaadin.flow.component.JsonSerializable;
import com.vaadin.flow.internal.JsonSerializer;
import elemental.json.Json;
import elemental.json.JsonObject;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.utils.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class JsonSerializerUtils {

    public static JsonObject toJson(Object object) {
        Class<?> objectClass = object.getClass();
        try {
            JsonObject json = Json.createObject();
            BeanInfo info = Introspector.getBeanInfo(objectClass);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if ("class".equals(pd.getName()) ||
                        ReflectionUtils.getField(objectClass, pd.getName())
                                .map(f -> f.isAnnotationPresent(TdJsonIgnore.class))
                                .orElse(false)) {
                    continue;
                }
                Method reader = pd.getReadMethod();
                if (reader != null) {
                    try {
                        json.put(pd.getName(), JsonSerializer.toJson(reader.invoke(object)));
                    } catch (org.hibernate.LazyInitializationException e) {
                        //continue;
                    }
                }
            }

            return json;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Could not serialize object of type " + objectClass
                            + " to JsonValue",
                    e);
        }
    }

    public static JsonSerializable readJson(JsonObject value) {
        return JsonSerializer.toObject(Subscription.class, value);
    }
}
