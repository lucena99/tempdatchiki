package ru.psv4.tempdatchiki.ui.views.recipientedit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.ui.components.EditableField;
import ru.psv4.tempdatchiki.ui.events.EditEvent;

@Tag("subscription-details")
@HtmlImport("src/views/recipientedit/subscription-details.html")
public class SubscriptionDetails extends PolymerTemplate<SubscriptionDetails.Model> {

	@Id("name")
	private EditableField nameField;

	public SubscriptionDetails(Subscription s) {
		getModel().setItem(s);
		nameField.addActionClickListener(e -> fireEvent(new EditEvent(this)));
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public interface Model extends TemplateModel {
		@Include({ "controller.name", "notifyOut", "notifyError" })
		void setItem(Subscription s);
	}
}
