/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.controlleredit;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.crud.EntityPresenter;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.EditableField;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.controllers.ControllersView;
import ru.psv4.tempdatchiki.ui.views.editors.*;
import ru.psv4.tempdatchiki.ui.views.recipientedit.SubscriptionDetails;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;
import ru.psv4.tempdatchiki.utils.RouteUtils;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("controller-details")
@HtmlImport("src/views/controlleredit/controller-details.html")
@Route(value = "controller", layout = MainView.class)
public class ControllerDetails extends PolymerTemplate<ControllerDetails.Model> implements HasUrlParameter<String> {

	@Id("backward")
	private Button backward;

    @Id("name")
	private EditableField nameField;

    @Id("url")
    private EditableField fcmTokenField;

    @Id("sensors")
    private Div sensorsDiv;

	private boolean isDirty;

	private final EntityPresenter<Controller, ControllersView> presenter;

	private Location currentLocation;

	@Autowired
	public ControllerDetails(EntityPresenter<Controller, ControllersView> presenter) {
		this.presenter = presenter;
		backward.addClickListener(e -> UI.getCurrent().navigate(ControllersView.class));
		nameField.addActionClickListener(e -> navigateEditor("name", presenter.getEntity().getName()));
		fcmTokenField.addActionClickListener(e -> navigateEditor("url", presenter.getEntity().getUrl()));
	}

	private void navigateEditor(String property, String value) {
		RouteUtils.route(StringFieldEditor.class, createEditorParameters(property, value));
	}

	private QueryParameters createEditorParameters(String property, String value) {
		TextFieldInitValues values = new TextFieldInitValues();
		values.setUid(presenter.getEntity().getUid());
		values.setBackwardUrl(currentLocation.getPath());
		values.setEntityClass(Controller.class.getSimpleName());
		values.setProperty(property);
		values.setValue(value);
		return TextFieldInitValues.convert(values);
	}

	public void display(Controller controller) {
		getModel().setItem(controller);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public interface Model extends TemplateModel {
		@Include({ "url",
			"name", "sensors.name", "sensors.minValue", "sensors.maxValue" })
		void setItem(Controller r);
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String uid) {
		if (uid != null) {
			presenter.loadEntity(uid, e -> {
                getModel().setItem(e);
                for (Sensor s : e.getSensors()) {
                    SensorDetails details = new SensorDetails(s);
					details.addEditListener(ev -> navigateEditor(s));
                    sensorsDiv.add(details);
				}
                Button button = new Button("Добавить датчик", new Icon(VaadinIcon.PLUS));
                button.setThemeName("tertiary");
				sensorsDiv.add(button);
				currentLocation = event.getLocation();
            });
		}
	}

	private void navigateEditor(Sensor sensor) {
		RouteUtils.route(SensorFieldEditor.class, createEditorParameters(sensor));
	}

	private QueryParameters createEditorParameters(Sensor sensor) {
		SensorFieldInitValues values = new SensorFieldInitValues();
		values.setName(sensor.getName());
		values.setControllerUid(sensor.getController().getUid());
		values.setMinValue(sensor.getMinValue());
		values.setMaxValue(sensor.getMaxValue());
		values.setBackwardUrl(currentLocation.getPath());
		return SensorFieldInitValues.convert(values);
	}
}
