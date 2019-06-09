package ru.psv4.tempdatchiki.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AbstractAppRouterLayout;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import ru.psv4.tempdatchiki.security.SecurityUtils;
import ru.psv4.tempdatchiki.ui.views.settingsview.SettingsView;

import static ru.psv4.tempdatchiki.utils.TdConst.*;

@Viewport(VIEWPORT)
@PWA(name = "Sensors App", shortName = "SensorsApp",
		startPath = "login",
		backgroundColor = "#227aef", themeColor = "#227aef",
		offlinePath = "offline-page.html",
		offlineResources = {"images/offline-login-banner.jpg"})
public class MainView extends AbstractAppRouterLayout {

	@Override
	protected void configure(AppLayout appLayout, AppLayoutMenu menu) {
		appLayout.setBranding(new Span("Sensors App"));

		if (SecurityUtils.isUserLoggedIn()) {
			setMenuItem(menu, new AppLayoutMenuItem(VaadinIcon.HEART.create(), TITLE_CONTROLLERS, PAGE_CONTROLLERS));
			setMenuItem(menu, new AppLayoutMenuItem(VaadinIcon.BOLT.create(), TITLE_INCIDENTS, PAGE_INCIDENTS));
			setMenuItem(menu, new AppLayoutMenuItem(VaadinIcon.USERS.create(), TITLE_RECIPIENTS, PAGE_RECIPIENTS));
			if (SecurityUtils.isAccessGranted(SettingsView.class)) {
				setMenuItem(menu, new AppLayoutMenuItem(VaadinIcon.SUN_O.create(), TITLE_SETTINGS, PAGE_SETTINGS));
			}
			setMenuItem(menu, new AppLayoutMenuItem(VaadinIcon.EXIT.create(), TITLE_LOGOUT, e ->
					UI.getCurrent().getPage().executeJavaScript("location.assign('/logout')")));
		}
		getElement().addEventListener("search-focus", e -> {
			appLayout.getElement().getClassList().add("hide-navbar");
		});

		getElement().addEventListener("search-blur", e -> {
			appLayout.getElement().getClassList().remove("hide-navbar");
		});
	}

	private void setMenuItem(AppLayoutMenu menu, AppLayoutMenuItem menuItem) {
		menuItem.getElement().setAttribute("theme", "icon-on-top");
		menu.addMenuItem(menuItem);
	}
}
