package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionEditor;

public class ControllerChangeEvent extends ComponentEvent<SubscriptionEditor> {

	private final Controller controller;

	public ControllerChangeEvent(SubscriptionEditor component, Controller controller) {
		super(component, false);
		this.controller = controller;
	}

	public Controller getController() {
		return controller;
	}
}