package ru.psv4.tempdatchiki.ui.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.psv4.tempdatchiki.security.Role;
import ru.psv4.tempdatchiki.security.SecurityUtils;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;
import ru.psv4.tempdatchiki.utils.TdConst;

@Route
@PageTitle("Температурные датчики")
@HtmlImport("styles/shared-styles.html")
@Viewport(TdConst.VIEWPORT)
public class LoginView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {

	private LoginOverlay login = new LoginOverlay();

	@Autowired
	public LoginView(@Value("${app.version}")String appVersion) {
		getElement().appendChild(login.getElement());

		LoginI18n i18n = LoginI18n.createDefault();
		i18n.setHeader(new LoginI18n.Header());
		i18n.getHeader().setTitle("Sensors App");
		i18n.getHeader().setDescription(String.format("v%s", appVersion));
		i18n.setAdditionalInformation(null);
		i18n.setForm(new LoginI18n.Form());
		i18n.getForm().setSubmit("Войти");
		i18n.getForm().setTitle("Войти");
		i18n.getForm().setUsername("Логин");
		i18n.getForm().setPassword("Пароль");
		login.setI18n(i18n);
		login.setForgotPasswordButtonVisible(false);
		login.setAction("/app/login");
		login.setOpened(true);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (SecurityUtils.isUserLoggedIn() &&
                SecurityUtils.isAnyAllowed(Role.getUIRoles())) {
			// Needed manually to change the URL because of https://github.com/vaadin/flow/issues/4189
			UI.getCurrent().getPage().getHistory().replaceState(null, "");
			event.rerouteTo(RecipientsView.class);
		}
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		login.setError(
			event.getLocation().getQueryParameters().getParameters().containsKey(
			        "error"));
	}
}
