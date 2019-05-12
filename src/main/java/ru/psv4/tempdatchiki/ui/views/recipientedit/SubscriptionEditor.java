package ru.psv4.tempdatchiki.ui.views.recipientedit;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.internal.AbstractFieldSupport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.dataproviders.ControllerDataProvider;
import ru.psv4.tempdatchiki.ui.events.ControllerChangeEvent;
import ru.psv4.tempdatchiki.ui.events.DeleteEvent;
import ru.psv4.tempdatchiki.ui.events.NotifyErrorChangeEvent;
import ru.psv4.tempdatchiki.ui.events.NotifyOverChangeEvent;

import java.util.Objects;
import java.util.stream.Stream;

@Tag("subscription-editor")
@HtmlImport("src/views/recipientedit/subscription-editor.html")
public class SubscriptionEditor extends PolymerTemplate<TemplateModel> implements HasValueAndElement<ComponentValueChangeEvent<SubscriptionEditor,Subscription>, Subscription> {

	@Id("controllers")
	private ComboBox<Controller> controllers;

	@Id("delete")
	private Button delete;

	@Id("over")
	private Checkbox over;

	@Id("error")
	private Checkbox error;

    private final AbstractFieldSupport<SubscriptionEditor, Subscription> fieldSupport;

	private BeanValidationBinder<Subscription> binder = new BeanValidationBinder<>(Subscription.class);

	public SubscriptionEditor(ControllerDataProvider controllerSource) {
		this.fieldSupport =  new AbstractFieldSupport<>(this, null,
				Objects::equals, c ->  {});
		controllers.setDataProvider(controllerSource);
		controllers.addValueChangeListener(e -> {
			fireEvent(new ControllerChangeEvent(this, e.getValue()));
		});

		over.addValueChangeListener(e -> fireEvent(new NotifyOverChangeEvent(this, e.getValue())));

		error.addValueChangeListener(e -> fireEvent(new NotifyErrorChangeEvent(this, e.getValue())));

		binder.forField(controllers).bind("controller");
		binder.forField(over).bind("notifyOver");
		binder.forField(error).bind("notifyError");
		controllers.setRequired(true);

		delete.addClickListener(e -> fireEvent(new DeleteEvent(this, false)));
	}
	
	@Override
	public void setValue(Subscription value) {
		fieldSupport.setValue(value);
		binder.setBean(value);
		boolean noControllerSelected = value == null || value.getController() == null;
		delete.setEnabled(!noControllerSelected);
	}

	@Override
	public Subscription getValue() {
		return fieldSupport.getValue();
	}

	public Stream<HasValue<?, ?>> validate() {
		return binder.validate().getFieldValidationErrors().stream().map(BindingValidationStatus::getField);
	}

	public Registration addControllerChangeListener(ComponentEventListener<ControllerChangeEvent> listener) {
		return addListener(ControllerChangeEvent.class, listener);
	}

	public Registration addNotifyOverChangeListener(ComponentEventListener<NotifyOverChangeEvent> listener) {
		return addListener(NotifyOverChangeEvent.class, listener);
	}

	public Registration addNotifyErrorChangeListener(ComponentEventListener<NotifyErrorChangeEvent> listener) {
		return addListener(NotifyErrorChangeEvent.class, listener);
	}

	public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		return addListener(DeleteEvent.class, listener);
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<SubscriptionEditor, Subscription>> listener) {
		return fieldSupport.addValueChangeListener(listener);
	}
}
