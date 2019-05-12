package ru.psv4.tempdatchiki.utils;

import org.springframework.data.domain.Sort;

import java.util.Locale;

public class TdConst {
    public static final Locale APP_LOCALE = new Locale("ru");

    public static final String PAGE_ROOT = "";
    public static final String PAGE_RECIPIENTS = "recipients";
    public static final String PAGE_RECIPIENT_EDIT = "recipients/edit";
    public static final String PAGE_CONTROLLERS = "controllers";
    public static final String PAGE_CONTROLLERS_EDIT = "controllers/edit";
    public static final String PAGE_USERS = "users";
    public static final String PAGE_PRODUCTS = "products";

    public static final String TITLE_RECIPIENTS = "Слушатели";
    public static final String TITLE_CONTROLLERS = "Контроллеры";
    public static final String TITLE_USERS = "Users";
    public static final String TITLE_PRODUCTS = "Products";
    public static final String TITLE_LOGOUT = "Выйти";
    public static final String TITLE_NOT_FOUND = "Page was not found";
    public static final String TITLE_ACCESS_DENIED = "Access denied";

    public static final String[] REFERENCE_SORT_FIELDS = {"createdDatetime"};
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

    public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes";

    // Mutable for testing.
    public static int NOTIFICATION_DURATION = 4000;
}
