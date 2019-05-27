package ru.psv4.tempdatchiki.ui.views.editors;

import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;

import java.util.function.Consumer;

public class RecipientNameSaver implements Saver {

    private CrudEntityPresenter<Recipient> presenter;

    RecipientNameSaver(CrudEntityPresenter<Recipient> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void save(String uid, String newValue, Runnable onSuccess, Runnable onFail) {
        boolean loadResult = presenter.loadEntity(uid, entity -> {
            entity.setName(newValue);
            presenter.save(entity, e -> onSuccess.run(), e -> onFail.run());
        });
        if (!loadResult) {
            onFail.run();
        }
    }
}
