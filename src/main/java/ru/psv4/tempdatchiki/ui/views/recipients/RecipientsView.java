package ru.psv4.tempdatchiki.ui.views.recipients;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.HasLogger;
import ru.psv4.tempdatchiki.backend.data.EntityUtil;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.EntityView;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.stream.Stream;

@Tag("recipients-view")
@HtmlImport("src/views/recipients/recipients-view.html")
@Route(value = TdConst.PAGE_RECIPIENTS, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_RECIPIENT_EDIT, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_ROOT, layout = MainView.class)
@PageTitle(TdConst.TITLE_RECIPIENTS)
public class RecipientsView extends PolymerTemplate<TemplateModel>
		implements HasLogger, HasUrlParameter<String>, EntityView<Recipient> {

	@Id("search")
	private SearchBar searchBar;

	@Id("grid")
	private Grid<Recipient> grid;

	@Id("dialog")
	private Dialog dialog;
//
	private ConfirmDialog confirmation;

//	private final RecipientEditor recipientEditor;
//
//	private final OrderDetails orderDetails = new OrderDetails();
//
	private final RecipientPresenter presenter;

	@Autowired
	public RecipientsView(RecipientPresenter presenter/*, RecipientEditor recipientEditor*/) {
		this.presenter = presenter;
//		this.recipientEditor = recipientEditor;

		searchBar.setActionText("Новый слушатель");
		searchBar.setPlaceHolder("Search");

		grid.setSelectionMode(Grid.SelectionMode.NONE);

		grid.addColumn(RecipientCard.getTemplate()
				.withProperty("recipientCard", RecipientCard::create)
				.withEventHandler("cardClick",
						r -> UI.getCurrent().navigate(TdConst.PAGE_RECIPIENTS + "/" + r.getUid())));
//
//		getSearchBar().addFilterChangeListener(
//				e -> presenter.filterChanged(getSearchBar().getFilter(), getSearchBar().isCheckboxChecked()));
//		getSearchBar().addActionClickListener(e -> presenter.createNewOrder());
//
		presenter.init(this);
//
//		dialog.addDialogCloseActionListener(e -> presenter.cancel());
	}

	@Override
	public ConfirmDialog getConfirmDialog() {
		return confirmation;
	}

	@Override
	public void setConfirmDialog(ConfirmDialog confirmDialog) {
		this.confirmation = confirmDialog;
	}

//	void setOpened(boolean opened) {
//		dialog.setOpened(opened);
//	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String recipientId) {
//		boolean editView = event.getLocation().getPath().contains(TdConst.PAGE_RECIPIENT_EDIT);
//		if (orderId != null) {
//			presenter.onNavigation(orderId, editView);
//		} else if (dialog.isOpened()) {
//			presenter.closeSilently();
//		}
	}

	void navigateToMainView() {
		getUI().ifPresent(ui -> ui.navigate(TdConst.PAGE_RECIPIENTS));
	}

	@Override
	public boolean isDirty() {
		return false /*orderEditor.hasChanges() || orderDetails.isDirty()*/;
	}

	@Override
	public void write(Recipient entity) throws ValidationException {
//		orderEditor.write(entity);
	}

	public Stream<HasValue<?, ?>> validate() {
		return Stream.empty();//orderEditor.validate();
	}

	SearchBar getSearchBar() {
		return searchBar;
	}

//	OrderEditor getOpenedOrderEditor() {
//		return orderEditor;
//	}
//
//	OrderDetails getOpenedOrderDetails() {
//		return orderDetails;
//	}

	Grid<Recipient> getGrid() {
		return grid;
	}

	@Override
	public void clear() {
//		orderDetails.setDirty(false);
//		orderEditor.clear();
	}

	void setDialogElementsVisibility(boolean editing) {
//		dialog.add(editing ? orderEditor : orderDetails);
//		orderEditor.setVisible(editing);
//		orderDetails.setVisible(!editing);
	}

	@Override
	public String getEntityName() {
		return EntityUtil.getName(Recipient.class);
	}
}
