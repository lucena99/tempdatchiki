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
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.crud.EntityPresenter;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.EditableField;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.editors.InitValues;
import ru.psv4.tempdatchiki.ui.views.editors.StringFieldEditor;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("recipient-details")
@HtmlImport("src/views/recipientedit/recipient-details.html")
@Route(value = "recipient", layout = MainView.class)
public class RecipientDetails extends PolymerTemplate<RecipientDetails.Model> implements HasUrlParameter<String> {

	private Recipient recipient;

	@Id("backward")
	private Button backward;

    @Id("name")
	private EditableField nameField;

    @Id("fcmToken")
    private EditableField fcmTokenField;

    @Id("subscriptions")
    private Div subsrDiv;

	private boolean isDirty;

	private final EntityPresenter<Recipient, RecipientsView> presenter;

	private Location currentLocation;

	@Autowired
	public RecipientDetails(EntityPresenter<Recipient, RecipientsView> presenter) {
		this.presenter = presenter;
		backward.addClickListener(e -> UI.getCurrent().navigate(RecipientsView.class));
		nameField.addActionClickListener(e -> navigateEditor("name", presenter.getEntity().getName()));
		fcmTokenField.addActionClickListener(e -> navigateEditor("fcmToken", presenter.getEntity().getFcmToken()));
	}

	private void navigateEditor(String property, String value) {
		UI.getCurrent().navigate(
				RouteUtil.getRoutePath(StringFieldEditor.class, StringFieldEditor.class.getAnnotation(Route.class)),
				createEditorParameters(property, value));
	}

	private QueryParameters createEditorParameters(String property, String value) {
		InitValues values = new InitValues();
		values.setUid(presenter.getEntity().getUid());
		values.setBackwardUrl(currentLocation.getPath());
		values.setEntityClass(Recipient.class.getSimpleName());
		values.setProperty(property);
		values.setValue(value);
		return InitValues.convert(values);
	}

	public void display(Recipient recipient) {
		this.recipient = recipient;
		getModel().setItem(recipient);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public interface Model extends TemplateModel {
		@Include({ "fcmToken",
			"name", "subscriptions.controller.name", "subscriptions.notifyOver", "subscriptions.notifyError" })
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
			presenter.loadEntity(uid, e -> {
                getModel().setItem(e);
                for (Subscription s : e.getSubscriptions()) {
                    SubscriptionDetails details = new SubscriptionDetails();
					details.addEditListener(ev -> {System.out.println(ev);});
                    subsrDiv.add(details);
					details.display(s);
				}
                Button button = new Button("Добавить подписку", new Icon(VaadinIcon.PLUS));
                button.setThemeName("tertiary");
				subsrDiv.add(button);
				currentLocation = event.getLocation();
            });
		}
	}
}
