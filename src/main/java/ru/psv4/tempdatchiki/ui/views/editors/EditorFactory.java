package ru.psv4.tempdatchiki.ui.views.editors;

import org.springframework.context.ApplicationContext;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.ui.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;

public class EditorFactory {
    public static Saver create(TextFieldInitValues initValues, HasNotifications view, ApplicationContext applicationContext) {
        switch (initValues.entityClass) {
            case "Recipient": {
                switch (initValues.property) {
                    case "name": return new EntityStringFieldSaver<Recipient>(
                            creareRecipientCrud(view, applicationContext), (c, v) -> { c.setName(v); });
                    case "fcmToken": return new EntityStringFieldSaver<Recipient>(
                            creareRecipientCrud(view, applicationContext), (c, v) -> { c.setFcmToken(v); });
                }
            }
            case "Controller": {
                switch (initValues.property) {
                    case "name": return new EntityStringFieldSaver<Controller>(
                            creareControllerCrud(view, applicationContext), (c, v) -> { c.setName(v); });
                    case "url": return new EntityStringFieldSaver<Controller>(
                            creareControllerCrud(view, applicationContext), (c, v) -> { c.setUrl(v); });
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static CrudEntityPresenter<Recipient> creareRecipientCrud(HasNotifications view, ApplicationContext applicationContext) {
        return new CrudEntityPresenter<>(
                applicationContext.getBean(RecipientService.class),
                applicationContext.getBean(CurrentUser.class),
                view);
    }

    private static CrudEntityPresenter<Controller> creareControllerCrud(HasNotifications view, ApplicationContext applicationContext) {
        return new CrudEntityPresenter<>(
                applicationContext.getBean(ControllerService.class),
                applicationContext.getBean(CurrentUser.class),
                view);
    }
}
