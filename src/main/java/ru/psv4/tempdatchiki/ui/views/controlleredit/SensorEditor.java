package ru.psv4.tempdatchiki.ui.views.controlleredit;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.internal.AbstractFieldSupport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.ui.events.*;

import java.util.Objects;
import java.util.stream.Stream;

@Tag("sensor-editor")
@HtmlImport("src/views/controlleredit/sensor-editor.html")
public class SensorEditor extends PolymerTemplate<TemplateModel> implements HasValueAndElement<ComponentValueChangeEvent<SensorEditor, Sensor>, Sensor> {

	@Id("name")
	private TextField name;

	@Id("delete")
	private Button delete;

    private final AbstractFieldSupport<SensorEditor, Sensor> fieldSupport;

	private BeanValidationBinder<Sensor> binder = new BeanValidationBinder<>(Sensor.class);

	public SensorEditor() {
		this.fieldSupport =  new AbstractFieldSupport<>(this, null,
				Objects::equals, c ->  {});

		binder.forField(name).bind("name");
		name.setRequired(true);

		name.addValueChangeListener(e -> fireEvent(new NameChangeEvent(this, e.getValue())));
		delete.addClickListener(e -> fireEvent(new DeleteEvent(this, false)));
	}
	
	@Override
	public void setValue(Sensor value) {
		fieldSupport.setValue(value);
		binder.setBean(value);
		boolean noNameSelected = value == null || value.getName() == null;
		delete.setEnabled(!noNameSelected);
	}

	@Override
	public Sensor getValue() {
		return fieldSupport.getValue();
	}

	public Stream<HasValue<?, ?>> validate() {
		return binder.validate().getFieldValidationErrors().stream().map(BindingValidationStatus::getField);
	}

	public Registration addNameChangeListener(ComponentEventListener<NameChangeEvent> listener) {
		return addListener(NameChangeEvent.class, listener);
	}

	public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
		return addListener(DeleteEvent.class, listener);
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<SensorEditor, Sensor>> listener) {
		return fieldSupport.addValueChangeListener(listener);
	}
}
