package ru.psv4.tempdatchiki.ui.views.recipients;

import com.vaadin.flow.data.renderer.TemplateRenderer;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;

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
public class RecipientCard {

	public static TemplateRenderer<Recipient> getTemplate() {
		return TemplateRenderer.of(
				  "<recipient-card"
				+ "  recipient-card='[[item.recipientCard]]'"
				+ "  on-card-click='cardClick'>"
				+ "</recipient-card>");
	}
	
	public static RecipientCard create(Recipient r) {
		return new RecipientCard(r);
	}

	private final Recipient r;

	public RecipientCard(Recipient r) {
		this.r = r;
	}

	public String getName() {
		return r.getName();
	}

	public List<Subscription> getSubscriptions() {
		return r.getSubscriptions();
	}
}
