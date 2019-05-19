package ru.psv4.tempdatchiki.ui.components;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

@Tag("temp-field")
@HtmlImport("src/components/temp-field.html")
public class TempField extends AbstractSinglePropertyField<TempField, Double> {

	public TempField() {
        super("value", null, true);
	}

	public void setEnabled(boolean enabled) {
		getElement().setProperty("disabled", !enabled);
	}

	public void setMin(double value) {
		getElement().setProperty("min", value);
	}

	public void setMax(double value) {
		getElement().setProperty("max", value);
	}

	public void setEditable(boolean editable) {
		getElement().setProperty("editable", editable);
	}

	public void setPattern(String pattern) {
		getElement().setProperty("pattern", pattern);
	}

}
