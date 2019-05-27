package ru.psv4.tempdatchiki.ui.views.editors;

import org.springframework.context.ApplicationContext;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;

public class EditorFactory {
    public static Saver create(TextFieldInitValues initValues, HasNotifications view, ApplicationContext applicationContext) {
        switch (initValues.entityClass) {
            case "Recipient": {
                switch (initValues.property) {
                    case "name": return createRecipientNameEditor(initValues, view, applicationContext);
                    case "fcmToken": return createRecipientFcmTokenEditor(initValues, view, applicationContext);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static RecipientNameSaver createRecipientNameEditor(TextFieldInitValues initValues, HasNotifications view, ApplicationContext applicationContext) {
        return new RecipientNameSaver(creareRecipientCrud(view, applicationContext));
    }

    private static RecipientFcmTokenSaver createRecipientFcmTokenEditor(TextFieldInitValues initValues, HasNotifications view, ApplicationContext applicationContext) {
        return new RecipientFcmTokenSaver(creareRecipientCrud(view, applicationContext));
    }

    private static CrudEntityPresenter<Recipient> creareRecipientCrud(HasNotifications view, ApplicationContext applicationContext) {
        return new CrudEntityPresenter<>(
                applicationContext.getBean(RecipientService.class),
                applicationContext.getBean(CurrentUser.class),
                view);
    }
}
