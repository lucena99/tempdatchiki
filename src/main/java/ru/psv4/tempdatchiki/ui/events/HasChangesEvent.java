package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionsEditor;

public class HasChangesEvent extends ComponentEvent<Component> {

	private boolean hasChanges;

	public HasChangesEvent(Component component, boolean hasChanges) {
		super(component, false);
		this.hasChanges = hasChanges;
	}

	public boolean isHasChanges() {
		return hasChanges;
	}
}