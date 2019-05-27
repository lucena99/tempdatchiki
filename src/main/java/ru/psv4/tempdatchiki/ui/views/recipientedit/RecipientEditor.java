package ru.psv4.tempdatchiki.ui.views.recipientedit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.SaveEvent;

import java.util.stream.Stream;

@Tag("recipient-editor")
@HtmlImport("src/views/recipientedit/recipient-editor.html")
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RecipientEditor extends PolymerTemplate<RecipientEditor.Model> {

	public interface Model extends TemplateModel {
		void setState(String state);
	}

	@Id("title")
	private H2 title;

	@Id("name")
	private TextField name;

	@Id("fcmToken")
	private TextArea fcmToken;

	@Id("cancel")
	private Button cancel;

	@Id("save")
	private Button save;

	@Id("subscriptionsContainer")
	private Div subscriptionsContainer;

	private SubscriptionsEditor subcriptionsEditor;

	private User currentUser;

	private BeanValidationBinder<Recipient> binder = new BeanValidationBinder<>(Recipient.class);

	@Autowired
	public RecipientEditor(ControllerGridDataProvider controllerDataProvider,
						   SubscribtionService subscribtionService, CurrentUser currentUser) {
		subcriptionsEditor = new SubscriptionsEditor(controllerDataProvider, subscribtionService, currentUser);
		subscriptionsContainer.add(subcriptionsEditor);

		cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
		save.addClickListener(e -> fireEvent(new SaveEvent(this, false)));

		name.setRequired(true);
		binder.bind(name, "name");

		binder.bind(fcmToken, "fcmToken");

		subcriptionsEditor.setRequiredIndicatorVisible(true);
		binder.bind(subcriptionsEditor, "subscriptions");

		subcriptionsEditor.addHasChangesListener(e -> save.setEnabled(hasChanges()));

		binder.addValueChangeListener(e -> {
			if (e.getOldValue() != null) {
				save.setEnabled(hasChanges());
			}
		});
	}

	public boolean hasChanges() {
		return binder.hasChanges() || subcriptionsEditor.hasChanges();
	}

	public void clear() {
		binder.readBean(null);
		subcriptionsEditor.setValue(null);
	}

	public void close() {}

	public void write(Recipient r) throws ValidationException {
		binder.writeBean(r);
	}

	public void read(Recipient r, boolean isNew) {
		binder.readBean(r);
		subcriptionsEditor.setRecipient(r);

		title.setVisible(isNew);

		save.setEnabled(false);
	}

	public Stream<HasValue<?, ?>> validate() {
		Stream<HasValue<?, ?>> errorFields = binder.validate().getFieldValidationErrors().stream()
				.map(BindingValidationStatus::getField);
		return Stream.concat(errorFields, subcriptionsEditor.validate());
	}

	public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
		return addListener(SaveEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
}
