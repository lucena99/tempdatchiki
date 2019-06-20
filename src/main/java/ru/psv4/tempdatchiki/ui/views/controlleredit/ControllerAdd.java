/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.controlleredit;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.ui.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.ui.views.controllers.ControllersView;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("controller-add")
@HtmlImport("src/views/controlleredit/controller-add.html")
@Route(value = "controller-add", layout = MainView.class)
public class ControllerAdd extends PolymerTemplate<TemplateModel> implements HasNotifications {

	private Recipient recipient;

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

	@Id("name")
	private TextField nameField;

	@Id("url")
	private TextArea urlField;

	private CrudEntityPresenter<Controller> crud;
	private Runnable backwardAction = () -> UI.getCurrent().navigate(ControllersView.class);

	@Autowired
	public ControllerAdd(ControllerService controllerService, CurrentUser currentUser) {
		crud = new CrudEntityPresenter<Controller>(controllerService, currentUser, this);

		nameField.setValueChangeMode(ValueChangeMode.EAGER);
		urlField.setValueChangeMode(ValueChangeMode.EAGER);

		backward.addClickListener((e) -> backwardAction.run());
		cancel.addClickListener((e) -> backwardAction.run());

		save.addClickListener(e -> saveAction());
	}

	private void saveAction() {
		Controller controller = new Controller();
		controller.setUid(UIDUtils.generate());
		controller.setCreatedDatetime(LocalDateTime.now());
		controller.setName(nameField.getValue());
		controller.setUrl(urlField.getValue());

		crud.save(controller,
			(c) -> {
				showNotification("Успешно сохранено!");
				backwardAction.run();
			},
			(c) -> {});
	}
}
