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
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

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

	@Id("num")
	private NumberField numField;

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

		numField.setPreventInvalidInput(true);

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
		switch (action) {
			case SAVE:
				Sensor sensor;
				if (initValues.aNew) {
					sensor = new Sensor();
					sensor.setUid(UIDUtils.generate());
					sensor.setCreatedDatetime(LocalDateTime.now());
					sensor.setController(controller);
				} else {
					sensor = sensorService.getRepository()
							.findByControllerAndNameIgnoreCase(controller, initValues.name).get();
				}
				sensor.setName(nameField.getValue());
				sensor.setMinValue(minField.getValue());
				sensor.setMaxValue(maxField.getValue());
				sensor.setNum(numField.getValue().intValue());
				presenter.save(sensor,
						(s) -> {
							showNotification("Успешно сохранено!");
							UI.getCurrent().navigate(initValues.backwardUrl);
						},
						(s) -> {});
				break;
			case DELETE:
				sensor = sensorService.getRepository()
						.findByControllerAndNameIgnoreCase(controller, initValues.name).get();
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

		getModel().setANew(initValues.aNew);

		String title;
		if (initValues.aNew) {
			title = controller.getName() + " : " + "Новый датчик";
		} else {
			title = controller.getName() + " : " + initValues.name;
		}
		getModel().setTitle(title);

		if (initValues.num != null) {
			numField.setValue((double) initValues.num);
		}

		if (initValues.minValue != null) {
			minField.setValue(initValues.minValue);
		}

		if (initValues.maxValue != null) {
			maxField.setValue(initValues.maxValue);
		}

		if (initValues.name != null) {
			nameField.setValue(initValues.name);
		}
	}

	public interface Model extends TemplateModel {
		void setTitle(String title);
		void setANew(boolean aNew);
	}
}
