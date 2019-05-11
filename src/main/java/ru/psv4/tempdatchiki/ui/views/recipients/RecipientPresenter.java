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
//		view.getOpenedOrderEditor().setCurrentUser(currentUser.getUser());
//		view.getOpenedOrderEditor().addCancelListener(e -> cancel());
//		view.getOpenedOrderEditor().addReviewListener(e -> review());
//		view.getOpenedOrderDetails().addSaveListenter(e -> save());
//		view.getOpenedOrderDetails().addCancelListener(e -> cancel());
//		view.getOpenedOrderDetails().addBackListener(e -> back());
//		view.getOpenedOrderDetails().addEditListener(e -> edit());
//		view.getOpenedOrderDetails().addCommentListener(e -> addComment(e.getMessage()));
	}

	public void filterChanged(String filter) {
		dataProvider.setFilter(new RecipientGridDataProvider.RecipientFilter(filter));
	}

	void onNavigation(String id, boolean edit) {
		entityPresenter.loadEntity(id, e -> open(e, edit));
	}

	void createNewRecipient() {
		open(entityPresenter.createNew(), true);
	}

	void cancel() {
		/*entityPresenter.cancel(() -> close(), () -> view.setOpened(true));*/
	}

	void closeSilently() {
		entityPresenter.close();
//		view.setOpened(false);
	}

	void edit() {
		UI.getCurrent().navigate(TdConst.PAGE_RECIPIENT_EDIT + "/" + entityPresenter.getEntity().getUid());
	}

	void back() {
		view.setDialogElementsVisibility(true);
	}

	void review() {
		// Using collect instead of findFirst to assure all streams are
		// traversed, and every validation updates its view
		List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
		if (fields.isEmpty()) {
			if (entityPresenter.writeEntity()) {
//				view.setDialogElementsVisibility(false);
//				view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
			}
		} else if (fields.get(0) instanceof Focusable) {
			((Focusable<?>) fields.get(0)).focus();
		}
	}

	void save() {
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

	void addComment(String comment) {
//		if (entityPresenter.executeUpdate(e -> orderService.addComment(currentUser.getUser(), e, comment))) {
//			// You can only add comments when in view mode, so reopening in that state.
//			open(entityPresenter.getEntity(), false);
//		}
	}

	private void open(Recipient order, boolean edit) {
//		view.setDialogElementsVisibility(edit);
//		view.setOpened(true);
//
//		if (edit) {
//			view.getOpenedOrderEditor().read(order, entityPresenter.isNew());
//		} else {
//			view.getOpenedOrderDetails().display(order, false);
//		}
	}

	private void close() {
//		view.getOpenedOrderEditor().close();
//		view.setOpened(false);
		view.navigateToMainView();
		entityPresenter.close();
	}
}
