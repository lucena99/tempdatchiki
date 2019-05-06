package ru.psv4.tempdatchiki.dto;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityClass {
    Class<?> value();
}
