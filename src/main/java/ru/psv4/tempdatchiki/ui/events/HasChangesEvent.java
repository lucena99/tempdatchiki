package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionsEditor;

public class HasChangesEvent extends ComponentEvent<SubscriptionsEditor> {

	private boolean hasChanges;

	public HasChangesEvent(SubscriptionsEditor component, boolean hasChanges) {
		super(component, false);
		this.hasChanges = hasChanges;
	}

	public boolean isHasChanges() {
		return hasChanges;
	}
}