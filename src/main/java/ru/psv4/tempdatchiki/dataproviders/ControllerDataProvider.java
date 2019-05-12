package ru.psv4.tempdatchiki.dataproviders;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.service.ControllerService;

import java.util.stream.Stream;

@SpringComponent
@UIScope
public class ControllerDataProvider extends AbstractBackEndDataProvider<Controller, String> {

	@Autowired
	private ControllerService controllerService;

	@Override
	protected int sizeInBackEnd(Query<Controller, String> query) {
		return (int) controllerService.countAnyMatching(query.getFilter());
	}

	@Override
	public Stream<Controller> fetchFromBackEnd(Query<Controller, String> query) {
		return controllerService.findAnyMatching(query.getFilter(), PageRequest.of(query.getOffset(), query.getLimit()))
				.stream();
	}

}
