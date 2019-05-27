package ru.psv4.tempdatchiki.ui.views.editors;

import java.util.function.Consumer;

public interface Saver<E> {
    public void save(String uid, String newValue, Runnable onSuccess, Runnable onFail);
}
