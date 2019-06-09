package ru.psv4.tempdatchiki.dataproviders;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Incident;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.IncidentService;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.List;
import java.util.Optional;

/**
 * A pageable order data provider.
 */
@SpringComponent
@UIScope
public class IncidentGridDataProvider extends FilterablePageableDataProvider<Incident, String> {

	@Autowired
	private IncidentService incidentService;
	private List<QuerySortOrder> defaultSortOrders;

	public IncidentGridDataProvider() {
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
	protected Page<Incident> fetchFromBackEnd(Query<Incident, String> query, Pageable pageable) {
		String filter = query.getFilter().orElse("");
		return incidentService.findAnyMatching(Optional.ofNullable(filter), pageable);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return defaultSortOrders;
	}

	@Override
	protected int sizeInBackEnd(Query<Incident, String> query) {
		String filter = query.getFilter().orElse("");
		return (int) incidentService.countAnyMatching(Optional.ofNullable(filter));
	}

	@Override
	public Object getId(Incident item) {
		return item.getUid();
	}
}
