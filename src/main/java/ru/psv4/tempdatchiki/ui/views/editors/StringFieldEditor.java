/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;

import java.util.List;
import java.util.Map;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("string-field-editor")
@HtmlImport("src/views/editors/string-field-editor.html")
@Route(value = "string-field-editor", layout = MainView.class)
public class StringFieldEditor extends PolymerTemplate<StringFieldEditor.Model> implements HasNotifications, HasUrlParameter<String> {

	private Recipient recipient;

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

    @Id("value")
	private TextArea valueField;

	private boolean isDirty;

	private ApplicationContext applicationContext;

	private InitValues initValues;

	private Saver<?> editor;

	private static final Logger log = LoggerFactory.getLogger(StringFieldEditor.class);

	@Autowired
	public StringFieldEditor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		valueField.setValueChangeMode(ValueChangeMode.EAGER);

		ComponentEventListener<ClickEvent<Button>> backwardAction = e -> UI.getCurrent().navigate(initValues.backwardUrl);
		backward.addClickListener(backwardAction);
		cancel.addClickListener(backwardAction);

		save.addClickListener(e -> saveAction());
	}

	private void saveAction() {
		editor.save(initValues.uid,
				valueField.getValue(),
				() -> UI.getCurrent().navigate(initValues.backwardUrl),
				() -> {});
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parametersMap = queryParameters.getParameters();

		initValues = new InitValues();
		try {
			initValues.uid = parametersMap.get("uid").get(0);
			initValues.backwardUrl = parametersMap.get("backwardUrl").get(0);
			initValues.entityClass = parametersMap.get("entityClass").get(0);
			initValues.property = parametersMap.get("property").get(0);
			initValues.value = parametersMap.get("value").get(0);
		} catch (Exception e) {
			log.error("Error open page", e);
			showNotification(e.getMessage());
			UI.getCurrent().navigate(RecipientsView.class);
			return;
		}

		getModel().setTitle(initValues.composeTitle());
		getModel().setValue(initValues.value);

		//TODO: не понимаю почему не изменяется автоматически
		valueField.setValue(initValues.value);

		editor = EditorFactory.create(initValues, this, applicationContext);
	}

	public interface Model extends TemplateModel {
		void setTitle(String title);
		void setValue(String value);
	}
}
