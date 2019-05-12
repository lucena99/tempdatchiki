package ru.psv4.tempdatchiki.ui.events;

import com.vaadin.flow.component.ComponentEvent;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionsEditor;

public class NewEditorEvent extends ComponentEvent<SubscriptionsEditor> {

	public NewEditorEvent(SubscriptionsEditor component) {
		super(component, false);
	}
}