package ru.psv4.tempdatchiki.dto;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityField {
    String value();
}
