package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
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
import org.springframework.context.ApplicationContext;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;

@Tag("subscription-field-editor")
@Route(value = "subscription-field-editor", layout = MainView.class)
@HtmlImport("src/views/editors/subscription-field-editor.html")
public class SubscriptionFieldEditor extends PolymerTemplate<SubscriptionFieldEditor.Model> implements HasUrlParameter<String>, HasNotifications {

	@Id("delete")
	private Button delete;

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

	@Id("out")
	private Checkbox out;

	@Id("error")
	private Checkbox error;

	private SubsriptionFieldInitValues initValues;
	private Recipient recipient;
	private Controller controller;

	private ApplicationContext applicationContext;

	private static final Logger log = LoggerFactory.getLogger(SubscriptionFieldEditor.class);

	@Autowired
	public SubscriptionFieldEditor(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		ComponentEventListener<ClickEvent<Button>> backwardAction = e -> UI.getCurrent().navigate(initValues.backwardUrl);
		backward.addClickListener(backwardAction);
		cancel.addClickListener(backwardAction);
		save.addClickListener(e -> saveDeleteAction(Action.SAVE));
		delete.addClickListener(e -> saveDeleteAction(Action.DELETE));
	}

	private enum Action {SAVE, DELETE};

	private void saveDeleteAction(Action action) {
		SubscribtionService subscribtionService = applicationContext.getBean(SubscribtionService.class);
		CrudEntityPresenter<Subscription> presenter = new CrudEntityPresenter<Subscription>(
				subscribtionService,
				applicationContext.getBean(CurrentUser.class),
				this);
		Subscription subscription = subscribtionService.get(recipient, controller).get();
		switch (action) {
			case SAVE:
				subscription.setNotifyOut(out.getValue());
				subscription.setNotifyError(error.getValue());
				presenter.save(subscription,
						(s) -> UI.getCurrent().navigate(initValues.backwardUrl),
						(s) -> {});
				break;
			case DELETE:
				ConfirmDialog
						.createQuestion()
						.withCaption("Подтверждение")
						.withMessage("Вы уверены, что хотите удалить подписку?")
						.withOkButton(() -> {
							presenter.delete(subscription,
									(s) -> UI.getCurrent().navigate(initValues.backwardUrl),
									(s) -> {});
						}, ButtonOption.focus(), ButtonOption.caption("YES"))
						.withCancelButton(ButtonOption.caption("NO"))
						.open();

				break;
		}
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		initValues = SubsriptionFieldInitValues.parse(event.getLocation().getQueryParameters());

		recipient = applicationContext.getBean(RecipientService.class).load(initValues.recipientUid);
		controller = applicationContext.getBean(ControllerService.class).load(initValues.controllerUid);
		getModel().setTitle(recipient.getName() + " : " + controller.getName());

		out.setValue(initValues.over);
		error.setValue(initValues.error);
	}

	public interface Model extends TemplateModel {
		void setTitle(String title);
	}
}
