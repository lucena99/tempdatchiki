/**
 *
 */
package ru.psv4.tempdatchiki.ui.crud;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.backend.service.SettingService;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.views.settingsview.SettingsView;

@Configuration
public class PresenterFactory {
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EntityPresenter<Setting, SettingsView> settingEntityPresenter(SettingService crudService, CurrentUser currentUser) {
		return new EntityPresenter<>(crudService, currentUser);
	}
}
