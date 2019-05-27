/**
 *
 */
package ru.psv4.tempdatchiki.crud;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.backend.service.ControllerService;
import ru.psv4.tempdatchiki.backend.service.RecipientService;
import ru.psv4.tempdatchiki.backend.service.SettingService;
import ru.psv4.tempdatchiki.backend.service.SubscribtionService;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.views.controllers.ControllersView;
import ru.psv4.tempdatchiki.ui.views.recipients.RecipientsView;
import ru.psv4.tempdatchiki.ui.views.settingsview.SettingsView;

@Configuration
public class PresenterFactory {

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Recipient, RecipientsView> recipientEntityPresenter(RecipientService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Controller, ControllersView> controllerEntityPresenter(ControllerService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Setting, SettingsView> settingEntityPresenter(SettingService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}
}
