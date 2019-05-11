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
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * A pageable order data provider.
 */
@SpringComponent
@UIScope
public class RecipientGridDataProvider extends FilterablePageableDataProvider<Recipient, RecipientGridDataProvider.RecipientFilter> {

	public static class RecipientFilter implements Serializable {
		private String filter;

		public String getFilter() {
			return filter;
		}

		public RecipientFilter(String filter) {
			this.filter = filter;
		}

		public static RecipientFilter getEmptyFilter() {
			return new RecipientFilter("");
		}
	}

	@Autowired
	private RecipientService recipientService;
	private List<QuerySortOrder> defaultSortOrders;

	public RecipientGridDataProvider() {
		setSortOrders(TdConst.DEFAULT_SORT_DIRECTION, TdConst.ORDER_SORT_FIELDS);
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
	protected Page<Recipient> fetchFromBackEnd(Query<Recipient, RecipientFilter> query, Pageable pageable) {
		RecipientFilter filter = query.getFilter().orElse(RecipientFilter.getEmptyFilter());
		Page<Recipient> page = recipientService.findAnyMatching(Optional.ofNullable(filter.getFilter()), pageable);
		return page;
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return defaultSortOrders;
	}

	@Override
	protected int sizeInBackEnd(Query<Recipient, RecipientFilter> query) {
		RecipientFilter filter = query.getFilter().orElse(RecipientFilter.getEmptyFilter());
		return (int) recipientService.countAnyMatching(Optional.ofNullable(filter.getFilter()));
	}

	private Optional<LocalDate> getFilterDate(boolean showPrevious) {
		if (showPrevious) {
			return Optional.empty();
		}

		return Optional.of(LocalDate.now().minusDays(1));
	}

	@Override
	public Object getId(Recipient item) {
		return item.getUid();
	}
}
