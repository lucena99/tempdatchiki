package ru.psv4.tempdatchiki.vaadin_json;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TdJsonIgnore {
}
