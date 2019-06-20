package ru.psv4.tempdatchiki.ui.dataproviders;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.List;
import java.util.Optional;

import static com.vaadin.flow.spring.scopes.VaadinUIScope.VAADIN_UI_SCOPE_NAME;

/**
 * A pageable order data provider.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UnsubscribedControllersProvider extends PageableDataProvider<Controller, String> {

	@Autowired
	private ControllerService controllerService;
	private Recipient recipient;
	private List<QuerySortOrder> defaultSortOrders;

	public UnsubscribedControllersProvider() {
		setSortOrders(TdConst.DEFAULT_SORT_DIRECTION, TdConst.REFERENCE_SORT_FIELDS);
	}

	private void setSortOrders(Sort.Direction direction, String[] properties) {
		QuerySortOrderBuilder builder = new QuerySortOrderBuilder();
		for (String property : properties) {
			if (direction.isAscending()) {
				builder.thenAsc(property);
			} else {
				builder.thenDesc(property);
			}
		}
		defaultSortOrders = builder.build();
	}

	@Override
	protected Page<Controller> fetchFromBackEnd(Query<Controller, String> query, Pageable pageable) {
		String filter = query.getFilter().orElse("");
		return controllerService.findAnyMatching(recipient, Optional.ofNullable(filter), pageable);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return defaultSortOrders;
	}

	@Override
	protected int sizeInBackEnd(Query<Controller, String> query) {
		String filter = query.getFilter().orElse("");
		return (int) controllerService.countAnyMatching(recipient, Optional.ofNullable(filter));
	}

	@Override
	public Object getId(Controller item) {
		return item.getUid();
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
}
