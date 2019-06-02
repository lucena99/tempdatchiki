package ru.psv4.tempdatchiki.ui.views.editors;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
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
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.dataproviders.UnsubscribedControllersProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

@Tag("subscription-field-add")
@Route(value = "subscription-field-add", layout = MainView.class)
@HtmlImport("src/views/editors/subscription-field-add.html")
public class SubscriptionFieldAdd extends PolymerTemplate<SubscriptionFieldAdd.Model> implements HasUrlParameter<String>, HasNotifications {

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

	@Id("over")
	private Checkbox over;

	@Id("error")
	private Checkbox error;

	@Id("controller")
	private ComboBox<Controller> controller;

	private SubsriptionFieldInitValues initValues;
	private Recipient recipient;

	private ApplicationContext applicationContext;
	private UnsubscribedControllersProvider controllerSource;

	private static final Logger log = LoggerFactory.getLogger(SubscriptionFieldAdd.class);

	@Autowired
	public SubscriptionFieldAdd(ApplicationContext applicationContext,
								UnsubscribedControllersProvider controllerSource) {
		this.applicationContext = applicationContext;
		this.controllerSource = controllerSource;

		controller.setDataProvider(controllerSource);

		ComponentEventListener<ClickEvent<Button>> backwardAction = e -> UI.getCurrent().navigate(initValues.backwardUrl);
		backward.addClickListener(backwardAction);
		cancel.addClickListener(backwardAction);

		save.addClickListener(e -> saveAction());
	}

	private void saveAction() {
		SubscribtionService subscribtionService = applicationContext.getBean(SubscribtionService.class);
		CrudEntityPresenter<Subscription> presenter = new CrudEntityPresenter<Subscription>(
				subscribtionService,
				applicationContext.getBean(CurrentUser.class),
				this);
		Subscription subscription = new Subscription();
		subscription.setUid(UIDUtils.generate());
		subscription.setCreatedDatetime(LocalDateTime.now());
		subscription.setRecipient(recipient);
		subscription.setController(controller.getValue());
		subscription.setNotifyOver(over.getValue());
		subscription.setNotifyError(error.getValue());
		presenter.save(subscription,
				(s) -> UI.getCurrent().navigate(initValues.backwardUrl),
				(s) -> {});
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		initValues = SubsriptionFieldInitValues.parse(event.getLocation().getQueryParameters());

		recipient = applicationContext.getBean(RecipientService.class).load(initValues.recipientUid);
		getModel().setTitle(recipient.getName() + " : Новая подписка");
		controllerSource.setRecipient(recipient);
	}

	public interface Model extends TemplateModel {
		void setTitle(String title);
	}
}
