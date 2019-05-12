package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionEditor;

public class NotifyErrorChangeEvent extends ComponentEvent<SubscriptionEditor> {

	private final boolean notifyError;

	public NotifyErrorChangeEvent(SubscriptionEditor component, boolean notifyError) {
		super(component, false);
		this.notifyError = notifyError;
	}

	public boolean isNotifyError() {
		return notifyError;
	}
}