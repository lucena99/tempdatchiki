package ru.psv4.tempdatchiki.ui.views.swagger;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import ru.psv4.tempdatchiki.security.SecurityUtils;
import ru.psv4.tempdatchiki.ui.components.TempDatchikiCookieConsent;
import ru.psv4.tempdatchiki.ui.views.storefront.StorefrontView;
import ru.psv4.tempdatchiki.utils.TdConst;

@Tag("swagger-view")
@Route(value = "swagger")
@PageTitle("Swagger")
@HtmlImport("src/views/swagger/swagger-view.html")
@Viewport(TdConst.VIEWPORT)
public class SwaggerView extends Component {

	public SwaggerView() {}
}
