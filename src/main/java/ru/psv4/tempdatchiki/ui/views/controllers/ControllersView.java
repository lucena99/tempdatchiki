package ru.psv4.tempdatchiki.ui.views.controllers;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.dataproviders.ControllerGridDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.HasLogger;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.utils.TdConst;

@Tag("controllers-view")
@HtmlImport("src/views/controllers/controllers-view.html")
@Route(value = TdConst.PAGE_CONTROLLERS, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_ROOT, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_CONTROLLERS_EDIT, layout = MainView.class)
@PageTitle(TdConst.TITLE_CONTROLLERS)
public class ControllersView  extends PolymerTemplate<TemplateModel> implements HasLogger, HasNotifications {

    @Id("search")
    private SearchBar searchBar;

    @Id("grid")
    private Grid<Controller> grid;

    private final CrudEntityPresenter<Controller> crud;

    @Autowired
    public ControllersView(ControllerService controllerService, CurrentUser currentUser, ControllerGridDataProvider dataProvider) {
        this.crud = new CrudEntityPresenter<>(controllerService, currentUser, this);

        searchBar.setActionText("Новый контроллер");
        searchBar.setPlaceHolder("Search");

        grid.setDataProvider(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(ControllerCard.getTemplate()
                .withProperty("controllerCard", ControllerCard::create)
                .withEventHandler("cardClick",
                        r -> UI.getCurrent().navigate("controller/" + r.getUid())));

        getSearchBar().addFilterChangeListener(
                e -> dataProvider.setFilter(e.getSource().getFilter()));
        getSearchBar().addActionClickListener(e -> {});
    }

    SearchBar getSearchBar() {
        return searchBar;
    }

    Grid<Controller> getGrid() {
        return grid;
    }
}
