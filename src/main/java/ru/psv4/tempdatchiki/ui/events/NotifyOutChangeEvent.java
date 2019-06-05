package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionEditor;

public class NotifyOutChangeEvent extends ComponentEvent<SubscriptionEditor> {

	private final boolean notifyOut;

	public NotifyOutChangeEvent(SubscriptionEditor component, boolean notifyOut) {
		super(component, false);
		this.notifyOut = notifyOut;
	}

	public boolean isNotifyOut() {
		return notifyOut;
	}
}