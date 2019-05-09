package ru.psv4.tempdatchiki.model;

import java.util.Collection;

public class SubscriptionRegset extends Regset<Subscription> {

    public SubscriptionRegset(Collection<Subscription> list) {super(list);}

    @Override
    protected void replaceValue(Subscription found, Subscription item) {
        found.setNotifyOver(item.isNotifyOver());
        found.setNotifyError(item.isNotifyError());
        found.setCreatedDatetime(item.getCreatedDatetime());
    }
}
