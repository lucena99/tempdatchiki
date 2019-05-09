package ru.psv4.tempdatchiki.ui.views.errors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.*;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.utils.TdConst;

import javax.servlet.http.HttpServletResponse;

@ParentLayout(MainView.class)
@PageTitle(TdConst.TITLE_NOT_FOUND)
@HtmlImport("styles/shared-styles.html")
public class CustomRouteNotFoundError extends RouteNotFoundError {

	public CustomRouteNotFoundError() {
		RouterLink link = Component.from(
				ElementFactory.createRouterLink("", "Go to the front page."),
				RouterLink.class);
		getElement().appendChild(new Text("Oops you hit a 404. ").getElement(), link.getElement());
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		return HttpServletResponse.SC_NOT_FOUND;
	}
}
