/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.recipientedit;

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
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.ui.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("recipient-add")
@HtmlImport("src/views/recipientedit/recipient-add.html")
@Route(value = "recipient-add", layout = MainView.class)
public class RecipientAdd extends PolymerTemplate<TemplateModel> implements HasNotifications {

	private Recipient recipient;

	@Id("backward")
	private Button backward;

	@Id("save")
	private Button save;

	@Id("cancel")
	private Button cancel;

	@Id("name")
	private TextField nameField;

	@Id("fcmToken")
	private TextArea fcmTokenField;

	private CrudEntityPresenter<Recipient> crud;
	private Runnable backwardAction = () -> UI.getCurrent().navigate(RecipientsView.class);

	@Autowired
	public RecipientAdd(RecipientService recipientService, CurrentUser currentUser) {
		crud = new CrudEntityPresenter<Recipient>(recipientService, currentUser, this);

		nameField.setValueChangeMode(ValueChangeMode.EAGER);
		fcmTokenField.setValueChangeMode(ValueChangeMode.EAGER);

		backward.addClickListener((e) -> backwardAction.run());
		cancel.addClickListener((e) -> backwardAction.run());

		save.addClickListener(e -> saveAction());
	}

	private void saveAction() {
		Recipient recipient = new Recipient();
		recipient.setUid(UIDUtils.generate());
		recipient.setCreatedDatetime(LocalDateTime.now());
		recipient.setName(nameField.getValue());
		recipient.setFcmToken(fcmTokenField.getValue());

		crud.save(recipient,
			(c) -> {
				showNotification("Успешно сохранено!");
				backwardAction.run();
			},
			(c) -> {});
	}
}
