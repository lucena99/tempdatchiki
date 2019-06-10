package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;

@Tag("sensor-field-editor")
@Route(value = "sensor-field-editor", layout = MainView.class)
@HtmlImport("src/views/editors/sensor-field-editor.html")
public class SensorFieldEditor extends PolymerTemplate<SensorFieldEditor.Model> implements HasUrlParameter<String>, HasNotifications {

	@Id("delete")
	private Button delete;

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

	@Id("name")
	private TextField nameField;

	@Id("minValue")
	private NumberField minField;

	@Id("maxValue")
	private NumberField maxField;

	private SensorFieldInitValues initValues;
	private Controller controller;

	private SensorService sensorService;
	private CurrentUser currentUser;
	private ControllerService controllerService;

	private static final Logger log = LoggerFactory.getLogger(SensorFieldEditor.class);

	private enum Action {SAVE, DELETE};

	@Autowired
	public SensorFieldEditor(SensorService sensorService, CurrentUser currentUser,
							 ControllerService controllerService) {
		this.sensorService = sensorService;
		this.currentUser = currentUser;
		this.controllerService = controllerService;

		ComponentEventListener<ClickEvent<Button>> backwardAction = e -> UI.getCurrent().navigate(initValues.backwardUrl);
		backward.addClickListener(backwardAction);
		cancel.addClickListener(backwardAction);

		save.addClickListener(e -> saveDeleteAction(Action.SAVE));
		delete.addClickListener(e -> saveDeleteAction(Action.DELETE));

		minField.setValueChangeMode(ValueChangeMode.EAGER);
		maxField.setValueChangeMode(ValueChangeMode.EAGER);

		minField.setPattern("\\d\\\\.\\d");
		minField.setPreventInvalidInput(true);

		maxField.setPattern("\\d\\\\.\\d");
		maxField.setPreventInvalidInput(true);
	}

	private void saveDeleteAction(Action action) {
		CrudEntityPresenter<Sensor> presenter = new CrudEntityPresenter<Sensor>(
				sensorService,
				currentUser,
				this);
		Sensor sensor = sensorService.getRepository()
				.findByControllerAndNameIgnoreCase(controller, initValues.name).get();
		switch (action) {
			case SAVE:
				sensor.setName(nameField.getValue());
				sensor.setMinValue(minField.getValue());
				sensor.setMaxValue(maxField.getValue());
				presenter.save(sensor,
						(s) -> {
							showNotification("Успешно сохранено!");
							UI.getCurrent().navigate(initValues.backwardUrl);
						},
						(s) -> {});
				break;
			case DELETE:
				ConfirmDialog
						.createQuestion()
						.withCaption("Подтверждение")
						.withMessage("Вы уверены, что хотите удалить датчик?")
						.withOkButton(() -> {
							presenter.delete(sensor,
									(s) -> {
										showNotification("Успешно удалено!");
										UI.getCurrent().navigate(initValues.backwardUrl);
									},
									(s) -> {});
						}, ButtonOption.focus(), ButtonOption.caption("YES"))
						.withCancelButton(ButtonOption.caption("NO"))
						.open();

				break;
		}
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		initValues = SensorFieldInitValues.parse(event.getLocation().getQueryParameters());

		controller = controllerService.load(initValues.controllerUid);
		getModel().setTitle(controller.getName() + " : " + initValues.name);

		minField.setValue(initValues.minValue);
		maxField.setValue(initValues.maxValue);
		nameField.setValue(initValues.name);
	}

	public interface Model extends TemplateModel {
		void setTitle(String title);
	}
}
