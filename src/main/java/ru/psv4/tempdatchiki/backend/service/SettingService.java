package ru.psv4.tempdatchiki.backend.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.backend.data.Setting;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.repositories.SettingRepository;
import ru.psv4.tempdatchiki.utils.UIDUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SettingService extends ReferenceService<Setting> implements CrudService<Setting>, InitializingBean {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private ApplicationContext applicationContext;

    public SettingService() {
        super(Setting.class);
    }

    @Override
    public SettingRepository getRepository() {
        return settingRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SettingService service = applicationContext.getBean(SettingService.class);
        for (String key : new String[]{
                Setting.EVENT_HUB_AUTHORIZATION_KEY,
                Setting.EVENT_HUB_URL,
                Setting.DB_VERSION}) {
            Optional<Setting> opSetting = service.getRepository().findByName(key);
            if (!opSetting.isPresent()) {
                Setting setting = new Setting();
                setting.setUid(UIDUtils.generate());
                setting.setCreatedDatetime(LocalDateTime.now());
                setting.setName(key);
                service.save(null, setting);
            }
        }
    }

    public Page<Setting> findAnyMatching(Optional<String> optionalFilter, Pageable pageable) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return settingRepository.findByNameContainingIgnoreCase(optionalFilter.get(), pageable);
        } else {
            return settingRepository.findAll(pageable);
        }
    }

    public long countAnyMatching(Optional<String> optionalFilter) {
        if (optionalFilter.isPresent() && !optionalFilter.get().isEmpty()) {
            return settingRepository.countByNameContainingIgnoreCase(optionalFilter.get());
        } else {
            return settingRepository.count();
        }
    }
}