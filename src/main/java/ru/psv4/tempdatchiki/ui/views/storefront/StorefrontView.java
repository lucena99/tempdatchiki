package ru.psv4.tempdatchiki.ui.views.storefront;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.HasLogger;
import ru.psv4.tempdatchiki.model.Subscription;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.EntityView;
import ru.psv4.tempdatchiki.utils.TdConst;

@Tag("storefront-view")
@HtmlImport("src/views/storefront/storefront-view.html")
@Route(value = TdConst.PAGE_STOREFRONT, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_STOREFRONT_EDIT, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_ROOT, layout = MainView.class)
@PageTitle(TdConst.TITLE_STOREFRONT)
public class StorefrontView extends PolymerTemplate<TemplateModel>
		implements HasLogger, HasUrlParameter<Long>, EntityView<Subscription> {

	@Id("search")
	private SearchBar searchBar;

	@Id("grid")
	private Grid<Subscription> grid;

//	@Id("dialog")
//	private Dialog dialog;
//
	private ConfirmDialog confirmation;
//
//	private final OrderEditor orderEditor;
//
//	private final OrderDetails orderDetails = new OrderDetails();
//
//	private final OrderPresenter presenter;

	@Autowired
	public StorefrontView(/*OrderPresenter presenter, OrderEditor orderEditor*/) {
//		this.presenter = presenter;
//		this.orderEditor = orderEditor;

		searchBar.setActionText("New order");
		searchBar.setCheckboxText("Show past orders");
		searchBar.setPlaceHolder("Search");

		grid.setSelectionMode(Grid.SelectionMode.NONE);

//		grid.addColumn(OrderCard.getTemplate()
//				.withProperty("orderCard", OrderCard::create)
//				.withProperty("header", order -> presenter.getHeaderByOrderId(order.getId()))
//				.withEventHandler("cardClick",
//						order -> UI.getCurrent().navigate(TdConst.PAGE_STOREFRONT + "/" + order.getId())));
//
//		getSearchBar().addFilterChangeListener(
//				e -> presenter.filterChanged(getSearchBar().getFilter(), getSearchBar().isCheckboxChecked()));
//		getSearchBar().addActionClickListener(e -> presenter.createNewOrder());
//
//		presenter.init(this);
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
	public void setParameter(BeforeEvent event, @OptionalParameter Long orderId) {
//		boolean editView = event.getLocation().getPath().contains(TdConst.PAGE_STOREFRONT_EDIT);
//		if (orderId != null) {
//			presenter.onNavigation(orderId, editView);
//		} else if (dialog.isOpened()) {
//			presenter.closeSilently();
//		}
	}

	void navigateToMainView() {
		getUI().ifPresent(ui -> ui.navigate(TdConst.PAGE_STOREFRONT));
	}

	@Override
	public boolean isDirty() {
		return false /*orderEditor.hasChanges() || orderDetails.isDirty()*/;
	}

	@Override
	public void write(Subscription entity) throws ValidationException {
//		orderEditor.write(entity);
	}

//	public Stream<HasValue<?, ?>> validate() {
//		return orderEditor.validate();
//	}

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

	Grid<Subscription> getGrid() {
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
		return Subscription.class.getSimpleName();
	}
}
