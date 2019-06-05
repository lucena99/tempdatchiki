package ru.psv4.tempdatchiki.backend.data;

import java.util.Collection;

public class SubscriptionRegset extends Regset<Subscription> {

    public SubscriptionRegset(Collection<Subscription> list) {super(list);}

    @Override
    protected void replaceValue(Subscription found, Subscription item) {
        found.setNotifyOut(item.isNotifyOut());
        found.setNotifyError(item.isNotifyError());
        found.setCreatedDatetime(item.getCreatedDatetime());
    }
}
