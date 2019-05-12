package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionEditor;

public class NotifyOverChangeEvent extends ComponentEvent<SubscriptionEditor> {

	private final boolean notifyOver;

	public NotifyOverChangeEvent(SubscriptionEditor component, boolean notifyOver) {
		super(component, false);
		this.notifyOver = notifyOver;
	}

	public boolean isNotifyOver() {
		return notifyOver;
	}
}