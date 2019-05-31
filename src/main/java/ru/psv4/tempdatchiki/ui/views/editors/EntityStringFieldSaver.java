package ru.psv4.tempdatchiki.ui.views.editors;

import ru.psv4.tempdatchiki.backend.data.TdEntity;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EntityStringFieldSaver<T extends TdEntity> implements Saver {

    private CrudEntityPresenter<T> presenter;
    private BiConsumer<T, String> setter;

    EntityStringFieldSaver(CrudEntityPresenter<T> presenter, BiConsumer<T, String> setter) {
        this.presenter = presenter;
    }

    @Override
    public void save(String uid, String newValue, Runnable onSuccess, Runnable onFail) {
        boolean loadResult = presenter.loadEntity(uid, entity -> {
            setter.accept(entity, newValue);
            presenter.save(entity, e -> onSuccess.run(), e -> onFail.run());
        });
        if (!loadResult) {
            onFail.run();
        }
    }
}
