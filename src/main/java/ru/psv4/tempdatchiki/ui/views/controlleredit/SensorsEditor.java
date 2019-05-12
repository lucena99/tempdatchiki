package ru.psv4.tempdatchiki.ui.views.controlleredit;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.AbstractFieldSupport;
import com.vaadin.flow.shared.Registration;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.events.HasChangesEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SensorsEditor extends Div implements HasValueAndElement<ComponentValueChangeEvent<SensorsEditor,List<Sensor>>, List<Sensor>> {

	private SensorEditor empty;
	private ControllerGridDataProvider controllerDataProvider;
	private SensorService service;
	private CurrentUser currentUser;
	private boolean hasChanges = false;
    private final AbstractFieldSupport<SensorsEditor, List<Sensor>> fieldSupport;
    private Controller controller;

	public SensorsEditor(ControllerGridDataProvider controllerDataProvider,
						 SensorService service, CurrentUser currentUser) {
		this.controllerDataProvider = controllerDataProvider;
		this.service = service;
		this.currentUser = currentUser;
		this.fieldSupport = new AbstractFieldSupport<>(this, Collections.emptyList(),
				Objects::equals, c ->  {});
	}

	@Override
	public void setValue(List<Sensor> items) {
		fieldSupport.setValue(items);
		removeAll();
		hasChanges = false;

		if (items != null) {
			items.forEach(this::createEditor);
		}
		createEmptyElement();
		setHasChanges(false);
	}

	private SensorEditor createEditor(Sensor value) {
		SensorEditor editor = new SensorEditor();
		getElement().appendChild(editor.getElement());
		editor.addNameChangeListener(e -> nameChanged((SensorEditor)e.getSource(), e.getName()));
		editor.addDeleteListener(e -> {
			SensorEditor sourceEditor = (SensorEditor)e.getSource();
			if (sourceEditor != empty) {
				remove(sourceEditor);
				Sensor sensor = sourceEditor.getValue();
				setValue(getValue().stream().filter(element -> element != sensor).collect(Collectors.toList()));
				setHasChanges(true);
			}
		});

		editor.setValue(value);
		return editor;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		HasValueAndElement.super.setReadOnly(readOnly);
		getChildren().forEach(e -> ((SensorEditor) e).setReadOnly(readOnly));
	}

	@Override
	public List<Sensor> getValue() {
		return fieldSupport.getValue();
	}

	private void nameChanged(SensorEditor item, String name) {
		setHasChanges(true);
		if (empty == item) {
			createEmptyElement();
			Sensor s = service.createNew(currentUser.getUser());
			s.setName(name);
			s.setController(controller);
			item.setValue(s);
			setValue(Stream.concat(getValue().stream(), Stream.of(s)).collect(Collectors.toList()));
		}
	}

	private void createEmptyElement() {
		empty = createEditor(null);
	}

	public boolean hasChanges() {
		return hasChanges;
	}

	private void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
		if (hasChanges) {
			fireEvent(new HasChangesEvent(this, hasChanges));
		}
	}

	public Stream<HasValue<?, ?>> validate() {
		return getChildren()
				.filter(component -> fieldSupport.getValue().size() == 0 || !component.equals(empty))
				.map(editor -> ((SensorEditor) editor).validate()).flatMap(stream -> stream);
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<SensorsEditor, List<Sensor>>> listener) {
		return fieldSupport.addValueChangeListener(listener);
	}

	public Registration addHasChangesListener(ComponentEventListener<HasChangesEvent> listener) {
		return addListener(HasChangesEvent.class, listener);
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}
