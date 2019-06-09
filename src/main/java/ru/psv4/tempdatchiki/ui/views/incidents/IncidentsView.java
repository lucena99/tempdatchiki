package ru.psv4.tempdatchiki.ui.views.incidents;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.EntityUtil;
import ru.psv4.tempdatchiki.backend.data.Incident;
import ru.psv4.tempdatchiki.ui.HasLogger;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.EntityView;
import ru.psv4.tempdatchiki.ui.views.controlleredit.ControllerDetails;
import ru.psv4.tempdatchiki.ui.views.controlleredit.ControllerEditor;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.stream.Stream;

@Tag("incidents-view")
@HtmlImport("src/views/incidents/incidents-view.html")
@Route(value = TdConst.PAGE_INCIDENTS, layout = MainView.class)
@PageTitle(TdConst.TITLE_INCIDENTS)
public class IncidentsView extends PolymerTemplate<TemplateModel> implements HasLogger {
    @Id("search")
    private SearchBar searchBar;

    @Id("grid")
    private Grid<Incident> grid;

    private final ControllerDetails details = new ControllerDetails(null);

    private final IncidentPresenter presenter;

    @Autowired
    public IncidentsView(IncidentPresenter presenter) {
        this.presenter = presenter;

        searchBar.setPlaceHolder("Search");

        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addColumn(IncidentCard.getTemplate()
                .withProperty("incidentCard", IncidentCard::create)
                .withEventHandler("cardClick", System.out::println));

        getSearchBar().addFilterChangeListener(
                e -> presenter.filterChanged(getSearchBar().getFilter()));
        presenter.init(this);
    }

    SearchBar getSearchBar() {
        return searchBar;
    }

    ControllerDetails getDetails() {
        return details;
    }

    Grid<Incident> getGrid() {
        return grid;
    }
}
