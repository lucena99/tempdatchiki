package ru.psv4.tempdatchiki.backend.data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public class Reference extends TdEntity {

    private String name;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    @Override
    public String toString() {
        return getUid() + " : " + name;
    }
}
