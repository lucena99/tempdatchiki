package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.backend.service.SensorService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;

@Tag("sensor-field-editor")
@Route(value = "sensor-field-editor", layout = MainView.class)
@HtmlImport("src/views/editors/sensor-field-editor.html")
public class SensorFieldEditor extends PolymerTemplate<SensorFieldEditor.Model> implements HasUrlParameter<String>, HasNotifications {

//	@Id("delete")
	private Button delete;

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

	@Id("minValue")
	private NumberField minValue;

	@Id("maxValue")
	private NumberField maxValue;

	private SensorFieldInitValues initValues;
	private Controller controller;

	private ApplicationContext applicationContext;

	private static final Logger log = LoggerFactory.getLogger(SensorFieldEditor.class);

	@Autowired
	public SensorFieldEditor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		ComponentEventListener<ClickEvent<Button>> backwardAction = e -> UI.getCurrent().navigate(initValues.backwardUrl);
		backward.addClickListener(backwardAction);
		cancel.addClickListener(backwardAction);

		save.addClickListener(e -> saveAction());

		minValue.setValueChangeMode(ValueChangeMode.EAGER);
		maxValue.setValueChangeMode(ValueChangeMode.EAGER);

		minValue.setPattern("\\d\\\\.\\d");
		minValue.setPreventInvalidInput(true);

		maxValue.setPattern("\\d\\\\.\\d");
		maxValue.setPreventInvalidInput(true);
	}

	private void saveAction() {
		SensorService sensorService = applicationContext.getBean(SensorService.class);
		CrudEntityPresenter<Sensor> presenter = new CrudEntityPresenter<Sensor>(
				sensorService,
				applicationContext.getBean(CurrentUser.class),
				this);
		Sensor sensor = sensorService.getRepository()
				.findByControllerAndNameIgnoreCase(controller, initValues.name).get();
		sensor.setMinValue(minValue.getValue());
		sensor.setMaxValue(maxValue.getValue());
		presenter.save(sensor,
				(s) -> UI.getCurrent().navigate(initValues.backwardUrl),
				(s) -> {});
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		initValues = SensorFieldInitValues.parse(event.getLocation().getQueryParameters());

		controller = applicationContext.getBean(ControllerService.class).load(initValues.controllerUid);
		getModel().setTitle(controller.getName() + " : " + initValues.name);

		minValue.setValue(initValues.minValue);
		maxValue.setValue(initValues.maxValue);
	}

	public interface Model extends TemplateModel {
		void setTitle(String title);
	}
}
