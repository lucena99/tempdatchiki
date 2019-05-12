package ru.psv4.tempdatchiki.ui.views.controlleredit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
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
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.SaveEvent;

import java.util.stream.Stream;

@Tag("controller-editor")
@HtmlImport("src/views/controlleredit/controller-editor.html")
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControllerEditor extends PolymerTemplate<TemplateModel> {

	@Id("title")
	private H2 title;

	@Id("controllerName")
	private TextField controllerName;

	@Id("controllerUid")
	private TextField controllerUid;

	@Id("cancel")
	private Button cancel;

	@Id("save")
	private Button save;

	@Id("sensorsContainer")
	private Div sensorsContainer;

	private SensorsEditor sensorsEditor;

	private User currentUser;

	private BeanValidationBinder<Controller> binder = new BeanValidationBinder<>(Controller.class);

	@Autowired
	public ControllerEditor(ControllerGridDataProvider controllerDataProvider,
							SensorService service, CurrentUser currentUser) {
		sensorsEditor = new SensorsEditor(controllerDataProvider, service, currentUser);
		sensorsContainer.add(sensorsEditor);

		cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
		save.addClickListener(e -> fireEvent(new SaveEvent(this, false)));

		controllerName.setRequired(true);
		binder.bind(controllerName, "name");

		controllerUid.setRequired(true);
		binder.bind(controllerUid, "uid");

		sensorsEditor.setRequiredIndicatorVisible(true);
		binder.bind(sensorsEditor, "sensors");

		sensorsEditor.addHasChangesListener(e -> { save.setEnabled(hasChanges());});

		binder.addValueChangeListener(e -> {
			if (e.getOldValue() != null) {
				save.setEnabled(hasChanges());
			}
		});
	}

	public boolean hasChanges() {
		return binder.hasChanges() || sensorsEditor.hasChanges();
	}

	public void clear() {
		binder.readBean(null);
		sensorsEditor.setValue(null);
	}

	public void close() {}

	public void write(Controller e) throws ValidationException {
		binder.writeBean(e);
	}

	public void read(Controller e, boolean isNew) {
		binder.readBean(e);
		sensorsEditor.setController(e);

		title.setVisible(isNew);

		save.setEnabled(false);
	}

	public Stream<HasValue<?, ?>> validate() {
		Stream<HasValue<?, ?>> errorFields = binder.validate().getFieldValidationErrors().stream()
				.map(BindingValidationStatus::getField);
		return Stream.concat(errorFields, sensorsEditor.validate());
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
