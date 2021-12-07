package com.example.emergencias.model;

public class User {
    String name;
    String password;
    boolean stay_connected = false;

    public User(String name, String password, boolean stay_connected) {
        this.name = name;
        this.password = password;
        this.stay_connected = stay_connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStay_connected() {
        return stay_connected;
    }

    public void setStay_connected(boolean stay_connected) {
        this.stay_connected = stay_connected;
    }
}
