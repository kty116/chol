package com.sgmcommunity.chlo24.dto;

import java.io.Serializable;

/**
 * Created by hunter on 2017-06-27.
 */

public class SettingDTO implements Serializable {

    private int update;
    private int push;
    private int login;

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public int getPush() {
        return push;
    }

    public void setPush(int push) {
        this.push = push;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }
}
