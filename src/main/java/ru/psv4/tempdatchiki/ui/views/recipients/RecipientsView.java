package ru.psv4.tempdatchiki.ui.views.recipients;

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
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.dataproviders.RecipientGridDataProvider;
import ru.psv4.tempdatchiki.ui.HasLogger;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.ui.views.recipientedit.RecipientAdd;
import ru.psv4.tempdatchiki.utils.TdConst;

@Tag("recipients-view")
@HtmlImport("src/views/recipients/recipients-view.html")
@Route(value = TdConst.PAGE_RECIPIENTS, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_RECIPIENT_EDIT, layout = MainView.class)
@PageTitle(TdConst.TITLE_RECIPIENTS)
public class RecipientsView extends PolymerTemplate<TemplateModel>
		implements HasLogger, HasNotifications {

	@Id("search")
	private SearchBar searchBar;

	@Id("grid")
	private Grid<Recipient> grid;

	@Autowired
	public RecipientsView(RecipientGridDataProvider dataProvider) {
		searchBar.setActionText("Новый слушатель");
		searchBar.setPlaceHolder("Search");

		grid.setDataProvider(dataProvider);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn(RecipientCard.getTemplate()
				.withProperty("recipientCard", RecipientCard::create)
				.withEventHandler("cardClick",
						r -> UI.getCurrent().navigate("recipient/" + r.getUid())));

		getSearchBar().addFilterChangeListener(e -> dataProvider.setFilter(e.getSource().getFilter()));
		getSearchBar().addActionClickListener(e -> { UI.getCurrent().navigate(RecipientAdd.class); } );
	}

	void navigateToMainView() {
		getUI().ifPresent(ui -> ui.navigate(TdConst.PAGE_RECIPIENTS));
	}

	SearchBar getSearchBar() {
		return searchBar;
	}

	Grid<Recipient> getGrid() {
		return grid;
	}
}