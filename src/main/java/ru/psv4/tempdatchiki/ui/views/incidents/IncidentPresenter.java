package ru.psv4.tempdatchiki.ui.views.incidents;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.crud.EntityPresenter;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.dataproviders.IncidentGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IncidentPresenter {

	private IncidentsView view;

	private final IncidentGridDataProvider dataProvider;
	private final CurrentUser currentUser;
	private final ControllerService service;

	@Autowired
	IncidentPresenter(ControllerService service, IncidentGridDataProvider dataProvider, CurrentUser currentUser) {
		this.service = service;
		this.dataProvider = dataProvider;
		this.currentUser = currentUser;
	}

	void init(IncidentsView view) {
		this.view = view;
		view.getGrid().setDataProvider(dataProvider);
	}

	public void filterChanged(String filter) {
		dataProvider.setFilter(filter);
	}
}
