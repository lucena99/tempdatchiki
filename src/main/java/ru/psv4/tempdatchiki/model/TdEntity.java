package ru.psv4.tempdatchiki.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class TdEntity {
    @Id
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TdEntity) {
            return uid.equals(((TdEntity)obj).uid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uid.hashCode() + 31;
    }
}
