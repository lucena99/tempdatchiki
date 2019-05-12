package ru.psv4.tempdatchiki.ui.views.controllers;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.crud.EntityPresenter;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControllerPresenter {

	private ControllersView view;

	private final EntityPresenter<Controller, ControllersView> entityPresenter;
	private final ControllerGridDataProvider dataProvider;
	private final CurrentUser currentUser;
	private final ControllerService service;

	@Autowired
	ControllerPresenter(ControllerService service, ControllerGridDataProvider dataProvider,
						EntityPresenter<Controller, ControllersView> entityPresenter, CurrentUser currentUser) {
		this.service = service;
		this.entityPresenter = entityPresenter;
		this.dataProvider = dataProvider;
		this.currentUser = currentUser;
	}

	void init(ControllersView view) {
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

	void createNew() {
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
		UI.getCurrent().navigate(TdConst.PAGE_CONTROLLERS_EDIT + "/" + entityPresenter.getEntity().getUid());
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

	private void open(Controller e, boolean edit) {
		view.setDialogElementsVisibility(edit);
		view.setOpened(true);

		if (edit) {
			view.getOpenedEditor().read(e, entityPresenter.isNew());
		} else {
			view.getOpenedDetails().display(e);
		}
	}

	private void close() {
		view.getOpenedEditor().close();
		view.setOpened(false);
		view.navigateToMainView();
		entityPresenter.close();
	}
}
