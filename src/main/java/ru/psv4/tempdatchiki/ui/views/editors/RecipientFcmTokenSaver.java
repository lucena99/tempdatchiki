package ru.psv4.tempdatchiki.ui.views.editors;

import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;

public class RecipientFcmTokenSaver implements Saver<Recipient> {

    private CrudEntityPresenter<Recipient> presenter;

    RecipientFcmTokenSaver(CrudEntityPresenter<Recipient> presenter) {
        this.presenter = presenter;
    }

    @Override
    public void save(String uid, String newValue, Runnable onSuccess, Runnable onFail) {
        boolean loadResult = presenter.loadEntity(uid, entity -> {
            entity.setFcmToken(newValue);
            presenter.save(entity, e -> onSuccess.run(), e -> onFail.run());
        });
        if (!loadResult) {
            onFail.run();
        }
    }
}
