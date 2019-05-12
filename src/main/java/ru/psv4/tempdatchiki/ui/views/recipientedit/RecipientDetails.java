/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.recipientedit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.RecipientUIUtil;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("recipient-details")
@HtmlImport("src/views/recipientedit/recipient-details.html")
public class RecipientDetails extends PolymerTemplate<RecipientDetails.Model> {

	private Recipient recipient;

	@Id("cancel")
	private Button cancel;

	@Id("edit")
	private Button edit;

	private boolean isDirty;

	public RecipientDetails() {
		cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
		edit.addClickListener(e -> fireEvent(new EditEvent(this)));
	}

	public void display(Recipient recipient) {
		this.recipient = recipient;
		getModel().setItem(recipient);
		getModel().setState(RecipientUIUtil.getState(recipient.getSubscriptions()));
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public interface Model extends TemplateModel {
		@Include({ "uid",
			"name", "subscriptions.controller.name", "subscriptions.notifyOver", "subscriptions.notifyError" })
		void setItem(Recipient r);

		void setState(String state);
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}
}
