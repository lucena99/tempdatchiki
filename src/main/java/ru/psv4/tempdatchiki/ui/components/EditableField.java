package ru.psv4.tempdatchiki.ui.components;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

@Tag("editable-field")
@HtmlImport("src/components/editable-field.html")
public class EditableField extends PolymerTemplate<TemplateModel> {

	@Id("edit")
	private Button editButton;

	public void addActionClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
		editButton.addClickListener(listener);
	}
}
