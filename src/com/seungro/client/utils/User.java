package com.seungro.client.utils;

import com.seungro.client.components.UserButton;

public class User {
    private String name;
    private Boolean auth = false;
    private UserButton btn;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isAuth() {
        return auth;
    }

    public void setAuth(Boolean auth) {
        this.auth = auth;
    }

    public UserButton getBtn() {
        return btn;
    }

    public void setBtn(UserButton btn) {
        this.btn = btn;
    }
}
