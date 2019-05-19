package ru.psv4.tempdatchiki.ui.views.settingsview;

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
import ru.psv4.tempdatchiki.HasLogger;
import ru.psv4.tempdatchiki.backend.data.EntityUtil;
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.components.SearchBar;
import ru.psv4.tempdatchiki.ui.views.EntityView;
import ru.psv4.tempdatchiki.ui.views.settingedit.SettingDetails;
import ru.psv4.tempdatchiki.ui.views.settingedit.SettingEditor;
import ru.psv4.tempdatchiki.utils.TdConst;

import java.util.stream.Stream;

@Tag("settings-view")
@HtmlImport("src/views/settings/settings-view.html")
@Route(value = TdConst.PAGE_SETTINGS, layout = MainView.class)
@RouteAlias(value = TdConst.PAGE_SETTINGS_EDIT, layout = MainView.class)
@PageTitle(TdConst.TITLE_SETTINGS)
public class SettingsView extends PolymerTemplate<TemplateModel>
		implements HasLogger, HasUrlParameter<String>, EntityView<Setting> {

	@Id("search")
	private SearchBar searchBar;

	@Id("grid")
	private Grid<Setting> grid;

	@Id("dialog")
	private Dialog dialog;

	//private ConfirmDialog confirmation;

	private final SettingEditor editor;

	private final SettingDetails details = new SettingDetails();

	private final SettingPresenter presenter;

	@Autowired
	public SettingsView(SettingPresenter presenter, SettingEditor editor) {
		this.presenter = presenter;
		this.editor = editor;

		searchBar.setPlaceHolder("Search");

		grid.setSelectionMode(Grid.SelectionMode.NONE);

		grid.addColumn(SettingCard.getTemplate()
				.withProperty("settingCard", SettingCard::create)
				.withEventHandler("cardClick",
						r -> UI.getCurrent().navigate(TdConst.PAGE_SETTINGS + "/" + r.getUid())));

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
		boolean editView = event.getLocation().getPath().contains(TdConst.PAGE_SETTINGS_EDIT);
		if (uid != null) {
			presenter.onNavigation(uid, editView);
		} else if (dialog.isOpened()) {
			presenter.closeSilently();
		}
	}

	void navigateToMainView() {
		getUI().ifPresent(ui -> ui.navigate(TdConst.PAGE_SETTINGS));
	}

	@Override
	public boolean isDirty() {
		return editor.hasChanges() || details.isDirty();
	}

	@Override
	public void write(Setting entity) throws ValidationException {
		editor.write(entity);
	}

	public Stream<HasValue<?, ?>> validate() {
		return editor.validate();
	}

	SearchBar getSearchBar() {
		return searchBar;
	}

	SettingEditor getEditor() {
		return editor;
	}

	SettingDetails getDetails() {
		return details;
	}

	Grid<Setting> getGrid() {
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
		return EntityUtil.getName(Setting.class);
	}
}