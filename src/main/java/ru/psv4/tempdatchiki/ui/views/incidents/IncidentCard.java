package ru.psv4.tempdatchiki.ui.views.incidents;

import com.vaadin.flow.data.renderer.TemplateRenderer;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Incident;
import ru.psv4.tempdatchiki.backend.data.IncidentType;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.utils.DateUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class IncidentCard {

	private final Incident i;

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

	public static TemplateRenderer<Incident> getTemplate() {
		return TemplateRenderer.of(
				  "<incident-card"
				+ "  incident-card='[[item.incidentCard]]'"
				+ "  on-card-click='cardClick'>"
				+ "</incident-card>");
	}

	public static IncidentCard create(Incident i) {
		return new IncidentCard(i);
	}

	public IncidentCard(Incident i) {
		this.i = i;
	}

	public Sensor getSensor() {
		return i.getSensor();
	}

	public String getName() {
		return String.format("%s : %s", i.getSensor().getController().getName(), i.getSensor().getName());
	}

	public String getDatetime() {
		return dtf.format(i.getCreatedDatetime());
	}

	public String getType() {
		return i.getType().getName();
	}

	public Double getValue() {
		return i.getValue();
	}

	public Double getMinValue() {
		return i.getMinValue();
	}

	public Double getMaxValue() {
		return i.getMaxValue();
	}
}
