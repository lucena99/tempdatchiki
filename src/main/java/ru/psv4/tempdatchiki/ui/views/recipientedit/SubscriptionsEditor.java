package ru.psv4.tempdatchiki.ui.views.recipientedit;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.internal.AbstractFieldSupport;
import com.vaadin.flow.shared.Registration;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Subscription;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.dataproviders.ControllerDataProvider;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.events.NewEditorEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubscriptionsEditor extends Div implements HasValueAndElement<ComponentValueChangeEvent<SubscriptionsEditor,List<Subscription>>, List<Subscription>> {

	private SubscriptionEditor empty;
	private ControllerDataProvider controllerDataProvider;
	private SubscribtionService subscribtionService;
	private CurrentUser currentUser;
	private int totalPrice = 0;
	private boolean hasChanges = false;
    private final AbstractFieldSupport<SubscriptionsEditor,List<Subscription>> fieldSupport;
    private Recipient recipient;
	
	public SubscriptionsEditor(ControllerDataProvider controllerDataProvider,
							   SubscribtionService subscribtionService, CurrentUser currentUser) {
		this.controllerDataProvider = controllerDataProvider;
		this.subscribtionService = subscribtionService;
		this.currentUser = currentUser;
		this.fieldSupport = new AbstractFieldSupport<>(this, Collections.emptyList(),
				Objects::equals, c ->  {}); 
	}

	@Override
	public void setValue(List<Subscription> items) {
		fieldSupport.setValue(items);
		removeAll();
		totalPrice = 0;
		hasChanges = false;

		if (items != null) {
			items.forEach(this::createEditor);
		}
		createEmptyElement();
		setHasChanges(false);
	}

	private SubscriptionEditor createEditor(Subscription value) {
		SubscriptionEditor editor = new SubscriptionEditor(controllerDataProvider);
		getElement().appendChild(editor.getElement());
		editor.addControllerChangeListener(e -> controllerChanged(e.getSource(), e.getController()));
		editor.addDeleteListener(e -> {
			SubscriptionEditor subscriptionEditor = (SubscriptionEditor)e.getSource();
			if (subscriptionEditor != empty) {
				remove(subscriptionEditor);
				Subscription subscription = subscriptionEditor.getValue();
				setValue(getValue().stream().filter(element -> element != subscription).collect(Collectors.toList()));
				setHasChanges(true);
			}
		});

		editor.setValue(value);
		return editor;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		HasValueAndElement.super.setReadOnly(readOnly);
		getChildren().forEach(e -> ((SubscriptionEditor) e).setReadOnly(readOnly));
	}

	@Override
	public List<Subscription> getValue() {
		return fieldSupport.getValue();
	}

	private void controllerChanged(SubscriptionEditor item, Controller controller) {
		setHasChanges(true);
		if (empty == item) {
			createEmptyElement();
			Subscription s = subscribtionService.createNew(currentUser.getUser());
			s.setController(controller);
			s.setRecipient(recipient);
			item.setValue(s);
			setValue(Stream.concat(getValue().stream(), Stream.of(s)).collect(Collectors.toList()));
			fireEvent(new NewEditorEvent(this));
		}
	}

	private void createEmptyElement() {
		empty = createEditor(null);
	}

	public boolean hasChanges() {
		return hasChanges;
	}

	private void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
		if (hasChanges) {
			fireEvent(new ru.psv4.tempdatchiki.ui.events.ValueChangeEvent(this));
		}
	}

	public Stream<HasValue<?, ?>> validate() {
		return getChildren()
				.filter(component -> fieldSupport.getValue().size() == 0 || !component.equals(empty))
				.map(editor -> ((SubscriptionEditor) editor).validate()).flatMap(stream -> stream);
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<SubscriptionsEditor, List<Subscription>>> listener) {
		return fieldSupport.addValueChangeListener(listener);
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
}
