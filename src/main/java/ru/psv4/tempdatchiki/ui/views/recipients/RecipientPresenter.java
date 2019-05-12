package ru.psv4.tempdatchiki.ui.views.recipients;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.crud.EntityPresenter;
import ru.psv4.tempdatchiki.dataproviders.RecipientGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RecipientPresenter {

	private RecipientsView view;

	private final EntityPresenter<Recipient, RecipientsView> entityPresenter;
	private final RecipientGridDataProvider dataProvider;
	private final CurrentUser currentUser;
	private final RecipientService recipientService;

	@Autowired
    RecipientPresenter(RecipientService recipientService, RecipientGridDataProvider dataProvider,
					   EntityPresenter<Recipient, RecipientsView> entityPresenter, CurrentUser currentUser) {
		this.recipientService = recipientService;
		this.entityPresenter = entityPresenter;
		this.dataProvider = dataProvider;
		this.currentUser = currentUser;
	}

	void init(RecipientsView view) {
		this.entityPresenter.setView(view);
		this.view = view;
		view.getGrid().setDataProvider(dataProvider);
		view.getOpenedEditor().setCurrentUser(currentUser.getUser());
		view.getOpenedEditor().addCancelListener(e -> cancel());
		view.getOpenedEditor().addSaveListener(e -> save());
		view.getOpenedDetails().addCancelListener(e -> cancel());
		view.getOpenedDetails().addEditListener(e -> edit());
	}

	public void filterChanged(String filter) {
		dataProvider.setFilter(filter);
	}

	void onNavigation(String id, boolean edit) {
		entityPresenter.loadEntity(id, e -> open(e, edit));
	}

	void createNewRecipient() {
		open(entityPresenter.createNew(), true);
	}

	void cancel() {
		entityPresenter.cancel(() -> close(), () -> view.setOpened(true));
	}

	void closeSilently() {
		entityPresenter.close();
		view.setOpened(false);
	}

	void edit() {
		UI.getCurrent().navigate(TdConst.PAGE_RECIPIENT_EDIT + "/" + entityPresenter.getEntity().getUid());
	}

	void save() {
		// Using collect instead of findFirst to assure all streams are
		// traversed, and every validation updates its view
		List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
		if (fields.isEmpty()) {
			if (entityPresenter.writeEntity()) {
				entityPresenter.save(e -> {
					if (entityPresenter.isNew()) {
						view.showCreatedNotification();
						dataProvider.refreshAll();
					} else {
						view.showUpdatedNotification();
						dataProvider.refreshItem(e);
					}
					close();
				});
			}
		} else if (fields.get(0) instanceof Focusable) {
			((Focusable<?>) fields.get(0)).focus();
		}
	}

	private void open(Recipient recipient, boolean edit) {
		view.setDialogElementsVisibility(edit);
		view.setOpened(true);

		if (edit) {
			view.getOpenedEditor().read(recipient, entityPresenter.isNew());
		} else {
			view.getOpenedDetails().display(recipient);
		}
	}

	private void close() {
		view.getOpenedEditor().close();
		view.setOpened(false);
		view.navigateToMainView();
		entityPresenter.close();
	}
}
