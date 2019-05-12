package ru.psv4.tempdatchiki.ui.views;

import org.springframework.util.CollectionUtils;
import ru.psv4.tempdatchiki.backend.data.Subscription;

import java.util.List;

public class RecipientUIUtil {

    public static final String STATE_inactive = "inactive";
    public static final String STATE_active = "active";

    public static String getState(List<Subscription> subscriptions) {
        return !CollectionUtils.isEmpty(subscriptions) ? STATE_active : STATE_inactive;
    }
}
