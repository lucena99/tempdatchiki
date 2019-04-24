package ru.psv4.tempdatchiki.utils;

import java.util.UUID;

public class UIDUtils {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
