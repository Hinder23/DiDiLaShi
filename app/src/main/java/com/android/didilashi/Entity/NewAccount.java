package com.android.didilashi.Entity;

/**
 * Created by Administrator on 2017/2/27.
 */

public class NewAccount {
    private String phone;
    private String username;
    private String password;
    private String code;
    public NewAccount() {

    }
    public NewAccount(String phone, String username, String password, String code) {
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
