package ru.psv4.tempdatchiki.ui.views.incidents;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Incident;
import ru.psv4.tempdatchiki.ui.dataproviders.IncidentGridDataProvider;
import ru.psv4.tempdatchiki.ui.HasLogger;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.utils.TdConst;

@Tag("incidents-view")
@HtmlImport("src/views/incidents/incidents-view.html")
@Route(value = TdConst.PAGE_INCIDENTS, layout = MainView.class)
@PageTitle(TdConst.TITLE_INCIDENTS)
public class IncidentsView extends PolymerTemplate<TemplateModel> implements HasLogger, HasNotifications {
    @Id("search")
    private SearchBar searchBar;

    @Id("grid")
    private Grid<Incident> grid;

    @Autowired
    public IncidentsView(IncidentGridDataProvider dataProvider) {
        searchBar.setPlaceHolder("Search");

        grid.setDataProvider(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.addColumn(IncidentCard.getTemplate()
                .withProperty("incidentCard", IncidentCard::create)
                .withEventHandler("cardClick",
                        r -> UI.getCurrent().navigate("incident/" + r.getUid())));

        getSearchBar().addFilterChangeListener(e -> dataProvider.setFilter(e.getSource().getFilter()));
    }

    SearchBar getSearchBar() {
        return searchBar;
    }

    Grid<Incident> getGrid() {
        return grid;
    }
}
