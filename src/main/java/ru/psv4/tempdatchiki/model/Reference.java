package ru.psv4.tempdatchiki.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Reference {
    @Id
    private String uid;
    private String name;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return uid + " : " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reference) {
            return uid.equals(((Reference)obj).uid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uid.hashCode() + 31;
    }
}
