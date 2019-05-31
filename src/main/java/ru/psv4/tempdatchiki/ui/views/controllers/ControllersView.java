package ru.psv4.tempdatchiki.ui.views.controllers;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.ui.HasLogger;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.EntityUtil;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.EntityView;
import ru.psv4.tempdatchiki.ui.views.controlleredit.ControllerDetails;
import ru.psv4.tempdatchiki.ui.views.controlleredit.ControllerDetails2;
import ru.psv4.tempdatchiki.ui.views.controlleredit.ControllerEditor;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.stream.Stream;

@Tag("controllers-view")
@HtmlImport("src/views/controllers/controllers-view.html")
@Route(value = TdConst.PAGE_CONTROLLERS, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_ROOT, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_CONTROLLERS_EDIT, layout = MainView.class)
@PageTitle(TdConst.TITLE_CONTROLLERS)
public class ControllersView  extends PolymerTemplate<TemplateModel>
        implements HasLogger, HasUrlParameter<String>, EntityView<Controller> {
    @Id("search")
    private SearchBar searchBar;

    @Id("grid")
    private Grid<Controller> grid;

    @Id("dialog")
    private Dialog dialog;

    private final ControllerEditor editor;

    private final ControllerDetails details = new ControllerDetails(null);

    private final ControllerPresenter presenter;

    @Autowired
    public ControllersView(ControllerPresenter presenter, ControllerEditor editor) {
        this.presenter = presenter;
        this.editor = editor;

        searchBar.setActionText("Новый контроллер");
        searchBar.setPlaceHolder("Search");

        grid.setSelectionMode(Grid.SelectionMode.NONE);

        grid.addColumn(ControllerCard.getTemplate()
                .withProperty("controllerCard", ControllerCard::create)
                .withEventHandler("cardClick",
                        r -> UI.getCurrent().navigate("controller/" + r.getUid())));

        getSearchBar().addFilterChangeListener(
                e -> presenter.filterChanged(getSearchBar().getFilter()));
        getSearchBar().addActionClickListener(e -> presenter.createNew());
//
        presenter.init(this);
//
        dialog.addDialogCloseActionListener(e -> presenter.cancel());
    }

    //	@Override
//	public ConfirmDialog getConfirmDialog() {
//		return confirmation;
//	}
//
//	@Override
//	public void setConfirmDialog(ConfirmDialog confirmDialog) {
//		this.confirmation = confirmDialog;
//	}

    void setOpened(boolean opened) {
        dialog.setOpened(opened);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String uid) {
        boolean editView = event.getLocation().getPath().contains(TdConst.PAGE_CONTROLLERS_EDIT);
        if (uid != null) {
            presenter.onNavigation(uid, editView);
        } else if (dialog.isOpened()) {
            presenter.closeSilently();
        }
    }

    void navigateToMainView() {
        getUI().ifPresent(ui -> ui.navigate(TdConst.PAGE_CONTROLLERS));
    }

    @Override
    public boolean isDirty() {
        return editor.hasChanges() || details.isDirty();
    }

    @Override
    public void write(Controller e) throws ValidationException {
        editor.write(e);
    }

    public Stream<HasValue<?, ?>> validate() {
        return editor.validate();
    }

    SearchBar getSearchBar() {
        return searchBar;
    }

    ControllerEditor getEditor() {
        return editor;
    }

    ControllerDetails getDetails() {
        return details;
    }

    Grid<Controller> getGrid() {
        return grid;
    }

    @Override
    public void clear() {
        details.setDirty(false);
        editor.clear();
    }

    void setDialogElementsVisibility(boolean editing) {
        dialog.add(editing ? editor : details);
        editor.setVisible(editing);
        details.setVisible(!editing);
    }

    @Override
    public String getEntityName() {
        return EntityUtil.getName(Controller.class);
    }
}
