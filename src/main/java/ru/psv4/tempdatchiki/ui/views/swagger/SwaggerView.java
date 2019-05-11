package ru.psv4.tempdatchiki.ui.views.swagger;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.*;
import ru.psv4.tempdatchiki.utils.TdConst;

@Tag("swagger-view")
@Route(value = "swagger")
@PageTitle("Swagger")
@HtmlImport("src/views/swagger/swagger-view.html")
@Viewport(TdConst.VIEWPORT)
public class SwaggerView extends Component {

	public SwaggerView() {}
}
