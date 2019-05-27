package ru.psv4.tempdatchiki.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

public class RouteUtils {
    public static void route(Class<? extends Component> navigationTarget, QueryParameters parameters) {
        UI ui = UI.getCurrent();
        RouteConfiguration configuration = RouteConfiguration.forRegistry(ui.getRouter().getRegistry());
        ui.navigate(configuration.getUrl(navigationTarget), parameters);
    }
}
