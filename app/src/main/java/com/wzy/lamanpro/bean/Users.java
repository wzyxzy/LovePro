package com.wzy.lamanpro.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Users {
    @Id
    private String id;
    private String name;
    private String account;
    private String password;
    private String email;

    @Generated(hash = 1168930687)
    public Users(String id, String name, String account, String password,
            String email) {
        this.id = id;
        this.name = name;
        this.account = account;
        this.password = password;
        this.email = email;
    }

    @Generated(hash = 2146996206)
    public Users() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
