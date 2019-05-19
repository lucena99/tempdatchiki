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
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.backend.service.SettingService;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.List;
import java.util.Optional;

/**
 * A pageable order data provider.
 */
@SpringComponent
@UIScope
public class SettingGridDataProvider extends FilterablePageableDataProvider<Setting, String> {

	@Autowired
	private SettingService settingService;
	private List<QuerySortOrder> defaultSortOrders;

	public SettingGridDataProvider() {
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
	protected Page<Setting> fetchFromBackEnd(Query<Setting, String> query, Pageable pageable) {
		String filter = query.getFilter().orElse("");
		return settingService.findAnyMatching(Optional.ofNullable(filter), pageable);
	}

	@Override
	protected List<QuerySortOrder> getDefaultSortOrders() {
		return defaultSortOrders;
	}

	@Override
	protected int sizeInBackEnd(Query<Setting, String> query) {
		String filter = query.getFilter().orElse("");
		return (int) settingService.countAnyMatching(Optional.ofNullable(filter));
	}

	@Override
	public Object getId(Setting item) {
		return item.getUid();
	}
}
