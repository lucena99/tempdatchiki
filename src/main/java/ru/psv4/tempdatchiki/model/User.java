package ru.psv4.tempdatchiki.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity @Table(name = "\"user\"")
public class User extends Reference {

    private String login;

    private String password;

    private String role;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
