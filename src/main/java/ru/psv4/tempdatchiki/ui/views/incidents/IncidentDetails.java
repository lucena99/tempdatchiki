/**
 *
 */
package ru.psv4.tempdatchiki.ui.views.incidents;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.templatemodel.Include;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import ru.psv4.tempdatchiki.backend.data.Incident;
import ru.psv4.tempdatchiki.backend.service.IncidentService;
import ru.psv4.tempdatchiki.ui.crud.CrudEntityPresenter;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.MainView;
import ru.psv4.tempdatchiki.ui.events.CancelEvent;
import ru.psv4.tempdatchiki.ui.events.EditEvent;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;

/**
 * The component displaying a full (read-only) summary of an order, and a comment
 * field to add comments.
 */
@Tag("incident-details")
@HtmlImport("src/views/incidents/incident-details.html")
@Route(value = "incident", layout = MainView.class)
public class IncidentDetails extends PolymerTemplate<IncidentDetails.Model> implements HasUrlParameter<String>, HasNotifications {

	@Id("backward")
	private Button backward;

	private final CrudEntityPresenter<Incident> crud;

	private Location currentLocation;

	@Autowired
	public IncidentDetails(IncidentService incidentService, CurrentUser currentUser) {
		crud = new CrudEntityPresenter<Incident>(incidentService, currentUser, this);
		backward.addClickListener(e -> UI.getCurrent().navigate(IncidentsView.class));
	}

	public interface Model extends TemplateModel {
		@Include({"sensor.name"})
		void setItem(Incident i);
	}

	public Registration addEditListener(ComponentEventListener<EditEvent> listener) {
		return addListener(EditEvent.class, listener);
	}

	public Registration addCancelListener(ComponentEventListener<CancelEvent> listener) {
		return addListener(CancelEvent.class, listener);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String uid) {
		if (uid != null) {
			crud.loadEntity(uid, e -> {
                getModel().setItem(e);
				currentLocation = event.getLocation();
            });
		}
	}
}
