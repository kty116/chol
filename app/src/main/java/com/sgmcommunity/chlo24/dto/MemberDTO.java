package com.sgmcommunity.chlo24.dto;

import java.io.Serializable;

/**
 * Created by hunter on 2017-05-29.
 */

public class MemberDTO implements Serializable {
    String userId;
    String userPw;
    String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
