package ru.psv4.tempdatchiki.ui.views.settingsview;

import com.vaadin.flow.data.renderer.TemplateRenderer;
import ru.psv4.tempdatchiki.backend.data.Setting;

/**
 * Help class to get ready to use TemplateRenderer for displaying order card list on the Storefront and Dashboard grids.
 * Using TemplateRenderer instead of ComponentRenderer optimizes the CPU and memory consumption.
 * <p>
 * In addition, component includes an optional header above the order card. It is used
 * to visually separate orders into groups. Technically all order cards are
 * equivalent, but those that do have the header visible create a visual group
 * separation.
 */
public class SettingCard {

	public static TemplateRenderer<Setting> getTemplate() {
		return TemplateRenderer.of(
				  "<setting-card"
				+ "  setting-card='[[item.settingCard]]'"
				+ "  on-card-click='cardClick'>"
				+ "</setting-card>");
	}
	
	public static SettingCard create(Setting e) {
		return new SettingCard(e);
	}

	private final Setting e;

	public SettingCard(Setting e) {
		this.e = e;
	}

	public String getName() {
		return e.getName();
	}

	public String getValue() {
		return e.getValue();
	}
}
