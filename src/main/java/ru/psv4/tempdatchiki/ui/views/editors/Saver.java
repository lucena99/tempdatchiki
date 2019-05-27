package ru.psv4.tempdatchiki.ui.views.editors;

public interface Saver {
    public void save(String uid, String newValue, Runnable onSuccess, Runnable onFail);
}
