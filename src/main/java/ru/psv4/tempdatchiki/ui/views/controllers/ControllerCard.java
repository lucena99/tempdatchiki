package ru.psv4.tempdatchiki.ui.views.controllers;

import com.vaadin.flow.data.renderer.TemplateRenderer;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.ui.views.RecipientUIUtil;

import java.util.List;

/**
 * Help class to get ready to use TemplateRenderer for displaying order card list on the Storefront and Dashboard grids.
 * Using TemplateRenderer instead of ComponentRenderer optimizes the CPU and memory consumption.
 * <p>
 * In addition, component includes an optional header above the order card. It is used
 * to visually separate orders into groups. Technically all order cards are
 * equivalent, but those that do have the header visible create a visual group
 * separation.
 */
public class ControllerCard {

	public static TemplateRenderer<Controller> getTemplate() {
		return TemplateRenderer.of(
				  "<controller-card"
				+ "  controller-card='[[item.controllerCard]]'"
				+ "  on-card-click='cardClick'>"
				+ "</controller-card>");
	}
	
	public static ControllerCard create(Controller c) {
		return new ControllerCard(c);
	}

	private final Controller c;

	public ControllerCard(Controller c) {
		this.c = c;
	}

	public String getName() {
		return c.getName();
	}

	public String getUrl() {
		return c.getUrl();
	}

	public List<Sensor> getSensors() {
		return c.getSensors();
	}
}
