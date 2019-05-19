package ru.psv4.tempdatchiki.ui.components;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

@Tag("num-field")
@HtmlImport("src/components/num-field.html")
public class NumField extends AbstractSinglePropertyField<NumField, Integer> {

	public NumField() {
        super("value", null, true);
	}

	public void setEnabled(boolean enabled) {
		getElement().setProperty("disabled", !enabled);
	}

	public void setMin(int value) {
		getElement().setProperty("min", value);
	}

	public void setMax(int value) {
		getElement().setProperty("max", value);
	}

	public void setEditable(boolean editable) {
		getElement().setProperty("editable", editable);
	}

	public void setPattern(String pattern) {
		getElement().setProperty("pattern", pattern);
	}

}
