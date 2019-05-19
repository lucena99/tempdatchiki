package ru.psv4.tempdatchiki.ui.views.settingedit;

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
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.SaveEvent;

import java.util.stream.Stream;

@Tag("setting-editor")
@HtmlImport("src/views/settingedit/setting-editor.html")
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingEditor extends PolymerTemplate<TemplateModel> {

	@Id("name")
	private TextField name;

	@Id("value")
	private TextArea value;

	@Id("cancel")
	private Button cancel;

	@Id("save")
	private Button save;

	private User currentUser;

	private BeanValidationBinder<Setting> binder = new BeanValidationBinder<>(Setting.class);

	@Autowired
	public SettingEditor(ControllerGridDataProvider controllerDataProvider,
						 SubscribtionService subscribtionService, CurrentUser currentUser) {
		cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
		save.addClickListener(e -> fireEvent(new SaveEvent(this, false)));

		name.setRequired(true);
		binder.bind(name, "name");

		value.setRequired(true);
		binder.bind(value, "value");

		binder.addValueChangeListener(e -> {
			if (e.getOldValue() != null) {
				save.setEnabled(hasChanges());
			}
		});
	}

	public boolean hasChanges() {
		return binder.hasChanges(); 
	}

	public void clear() {
		binder.readBean(null);
	}

	public void close() {}

	public void write(Setting e) throws ValidationException {
		binder.writeBean(e);
	}

	public void read(Setting e, boolean isNew) {
		binder.readBean(e);
		save.setEnabled(false);
	}

	public Stream<HasValue<?, ?>> validate() {
		return binder.validate().getFieldValidationErrors().stream()
				.map(BindingValidationStatus::getField);
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
