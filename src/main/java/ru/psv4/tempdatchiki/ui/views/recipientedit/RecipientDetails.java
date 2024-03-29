/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.recipientedit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.ui.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.EditableField;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.ui.views.editors.*;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;
import ru.psv4.tempdatchiki.utils.RouteUtils;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("recipient-details")
@HtmlImport("src/views/recipientedit/recipient-details.html")
@Route(value = "recipient", layout = MainView.class)
public class RecipientDetails extends PolymerTemplate<RecipientDetails.Model> implements HasUrlParameter<String>, HasNotifications {

	@Id("backward")
	private Button backward;

	@Id("delete")
	private Button delete;

    @Id("name")
	private EditableField nameField;

    @Id("fcmToken")
    private EditableField fcmTokenField;

    @Id("subscriptions")
    private Div subsrDiv;

	private Recipient recipient;

	private final CrudEntityPresenter<Recipient> crud;

	private Location currentLocation;

	@Autowired
	public RecipientDetails(RecipientService recipientService, CurrentUser currentUser) {
		crud = new CrudEntityPresenter<>(recipientService, currentUser, this);
		backward.addClickListener(e -> navigateRecipients());
		nameField.addActionClickListener(e -> navigateEditor("name", recipient.getName()));
		fcmTokenField.addActionClickListener(e -> navigateEditor("fcmToken", recipient.getFcmToken()));
		delete.addClickListener(e -> deleteAction());
	}

	private void deleteAction() {
		ConfirmDialog
				.createQuestion()
				.withCaption("Подтверждение")
				.withMessage("Вы уверены, что хотите удалить слушателя?")
				.withOkButton(() -> {
					crud.delete(
							recipient,
							(c) -> {
								showNotification("Успешно удалено!");
								navigateRecipients();
							},
							(c) -> {});
				}, ButtonOption.focus(), ButtonOption.caption("YES"))
				.withCancelButton(ButtonOption.caption("NO"))
				.open();
	}

	private void navigateRecipients() {
		UI.getCurrent().navigate(RecipientsView.class);
	}

	private void navigateEditor(String property, String value) {
		RouteUtils.route(StringFieldEditor.class, createEditorParameters(property, value));
	}

	private QueryParameters createEditorParameters(String property, String value) {
		TextFieldInitValues values = new TextFieldInitValues();
		values.setUid(recipient.getUid());
		values.setBackwardUrl(currentLocation.getPath());
		values.setEntityClass(Recipient.class.getSimpleName());
		values.setProperty(property);
		values.setValue(value);
		return TextFieldInitValues.convert(values);
	}

	@Deprecated
	public void display(Recipient recipient) {
	}

	public interface Model extends TemplateModel {
		@Include({ "fcmToken",
			"name", "subscriptions.controller.name", "subscriptions.notifyOut", "subscriptions.notifyError" })
		void setItem(Recipient r);
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String uid) {
		if (uid != null) {
			crud.loadEntity(uid, r -> {
				recipient = r;
                getModel().setItem(r);
                for (Subscription s : r.getSubscriptions()) {
                    SubscriptionDetails details = new SubscriptionDetails(s);
					details.addEditListener(e -> navigateEditor(s));
                    subsrDiv.add(details);
				}
                Button button = new Button("Добавить подписку", new Icon(VaadinIcon.PLUS));
                button.setThemeName("tertiary");
				button.addClickListener(e -> navigateAdd(r));
				subsrDiv.add(button);
				currentLocation = event.getLocation();
            });
		}
	}

	private void navigateEditor(Subscription subscription) {
		RouteUtils.route(SubscriptionFieldEditor.class, createEditorParameters(subscription));
	}

	private void navigateAdd(Recipient recipient) {
		RouteUtils.route(SubscriptionFieldAdd.class, createAddParameters(recipient));
	}

	private QueryParameters createEditorParameters(Subscription subscription) {
		SubsriptionFieldInitValues values = new SubsriptionFieldInitValues();
		values.setRecipientUid(subscription.getRecipient().getUid());
		values.setControllerUid(subscription.getController().getUid());
		values.setOver(subscription.isNotifyOut());
		values.setError(subscription.isNotifyError());
		values.setBackwardUrl(currentLocation.getPath());
		return SubsriptionFieldInitValues.convert(values);
	}

	private QueryParameters createAddParameters(Recipient recipient) {
		SubsriptionFieldInitValues values = new SubsriptionFieldInitValues();
		values.setRecipientUid(recipient.getUid());
		values.setBackwardUrl(currentLocation.getPath());
		return SubsriptionFieldInitValues.convert(values);
	}
}