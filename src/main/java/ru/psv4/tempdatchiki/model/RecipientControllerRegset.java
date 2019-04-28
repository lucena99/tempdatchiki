package ru.psv4.tempdatchiki.model;

import java.util.Collection;

public class RecipientControllerRegset extends Regset<RegRecipientController> {

    public RecipientControllerRegset(Collection<RegRecipientController> list) {super(list);}

    @Override
    protected void replaceValue(RegRecipientController found, RegRecipientController item) {
        found.setNotifyOver(item.isNotifyOver());
        found.setNotifyError(item.isNotifyError());
    }
}
