package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionEditor;

public class NameChangeEvent extends ComponentEvent<Component> {

	private final String name;

	public NameChangeEvent(Component component, String name) {
		super(component, false);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}