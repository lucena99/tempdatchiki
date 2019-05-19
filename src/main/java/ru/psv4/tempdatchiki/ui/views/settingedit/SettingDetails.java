/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.settingedit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("setting-details")
@HtmlImport("src/views/settingedit/setting-details.html")
public class SettingDetails extends PolymerTemplate<SettingDetails.Model> {

	private Setting setting;

	@Id("cancel")
	private Button cancel;

	@Id("edit")
	private Button edit;

	private boolean isDirty;

	public SettingDetails() {
		cancel.addClickListener(e -> fireEvent(new CancelEvent(this, false)));
		edit.addClickListener(e -> fireEvent(new EditEvent(this)));
	}

	public void display(Setting setting) {
		this.setting = setting;
		getModel().setItem(setting);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public interface Model extends TemplateModel {
		@Include({ "uid",
			"name", "value" })
		void setItem(Setting r);
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}
}
