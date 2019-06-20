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
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.ui.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.EditableField;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.ui.views.controllers.ControllersView;
import ru.psv4.tempdatchiki.ui.views.editors.SensorFieldEditor;
import ru.psv4.tempdatchiki.ui.views.editors.SensorFieldInitValues;
import ru.psv4.tempdatchiki.ui.views.editors.StringFieldEditor;
import ru.psv4.tempdatchiki.ui.views.editors.TextFieldInitValues;
import ru.psv4.tempdatchiki.utils.RouteUtils;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("controller-details")
@HtmlImport("src/views/controlleredit/controller-details.html")
@Route(value = "controller", layout = MainView.class)
public class ControllerDetails extends PolymerTemplate<ControllerDetails.Model> implements HasUrlParameter<String>, HasNotifications {

	@Id("backward")
	private Button backward;

	@Id("delete")
	private Button delete;

    @Id("name")
	private EditableField nameField;

    @Id("url")
    private EditableField fcmTokenField;

    @Id("sensors")
    private Div sensorsDiv;

	private Controller controller;

	private ControllerService controllerService;
	private SensorService sensorService;
	private CurrentUser currentUser;
	private CrudEntityPresenter<Controller> crud;

	private Location currentLocation;

	@Autowired
	public ControllerDetails(ControllerService controllerService, SensorService sensorService, CurrentUser currentUser) {
		this.controllerService = controllerService;
		this.sensorService = sensorService;
		this.currentUser = currentUser;

		crud = new CrudEntityPresenter<Controller>(controllerService, currentUser, this);

		backward.addClickListener(e -> navigateControllers());
		delete.addClickListener(e -> deleteAction());
		nameField.addActionClickListener(e -> navigateEditor("name", controller.getName()));
		fcmTokenField.addActionClickListener(e -> navigateEditor("url", controller.getUrl()));
	}

	private void deleteAction() {
		ConfirmDialog
				.createQuestion()
				.withCaption("Подтверждение")
				.withMessage("Вы уверены, что хотите удалить контроллер?")
				.withOkButton(() -> {
					crud.delete(
							controller,
							(c) -> {
								showNotification("Успешно удалено!");
								navigateControllers();
							},
							(c) -> {});
				}, ButtonOption.focus(), ButtonOption.caption("YES"))
				.withCancelButton(ButtonOption.caption("NO"))
				.open();
	}

	private void navigateControllers() {
		UI.getCurrent().navigate(ControllersView.class);
	}

	private void navigateEditor(String property, String value) {
		RouteUtils.route(StringFieldEditor.class, createEditorParameters(property, value));
	}

	private QueryParameters createEditorParameters(String property, String value) {
		TextFieldInitValues values = new TextFieldInitValues();
		values.setUid(controller.getUid());
		values.setBackwardUrl(currentLocation.getPath());
		values.setEntityClass(Controller.class.getSimpleName());
		values.setProperty(property);
		values.setValue(value);
		return TextFieldInitValues.convert(values);
	}

	public void display(Controller controller) {
		getModel().setItem(controller);
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
			crud.loadEntity(uid, c -> {
				controller = c;
                getModel().setItem(c);
                for (Sensor s : c.getSensors()) {
                    SensorDetails details = new SensorDetails(s);
					details.addEditListener(ev -> navigateEditor(s));
                    sensorsDiv.add(details);
				}
                Button button = new Button("Добавить датчик", new Icon(VaadinIcon.PLUS));
                button.setThemeName("tertiary");
				button.addClickListener(ev -> navigateAdd());
				sensorsDiv.add(button);
				currentLocation = event.getLocation();
            });
		}
	}

	private void navigateEditor(Sensor sensor) {
		RouteUtils.route(SensorFieldEditor.class, createEditorParameters(sensor));
	}

	private void navigateAdd() {
		RouteUtils.route(SensorFieldEditor.class, createAddParameters(controller));
	}

	private QueryParameters createEditorParameters(Sensor sensor) {
		SensorFieldInitValues values = new SensorFieldInitValues();
		values.setName(sensor.getName());
		values.setControllerUid(sensor.getController().getUid());
		values.setMinValue(sensor.getMinValue());
		values.setMaxValue(sensor.getMaxValue());
		values.setBackwardUrl(currentLocation.getPath());
		values.setaNew(false);
		values.setNum(sensor.getNum());
		return SensorFieldInitValues.convert(values);
	}

	private QueryParameters createAddParameters(Controller controller) {
		SensorFieldInitValues values = new SensorFieldInitValues();
		values.setControllerUid(controller.getUid());
		values.setBackwardUrl(currentLocation.getPath());
		values.setNum(sensorService.getRepository().getNextNum(controller));
		values.setaNew(true);
		return SensorFieldInitValues.convert(values);
	}
}
