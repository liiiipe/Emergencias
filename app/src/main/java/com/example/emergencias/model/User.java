package com.example.emergencias.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String name;
    String password;
    boolean stay_connected = false;
    ArrayList<Contact> contatos;

    public User(String name, String password, boolean stay_connected) {
        this.name = name;
        this.password = password;
        this.stay_connected = stay_connected;
        this.contatos = new ArrayList<Contact>();
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

    public ArrayList<Contact> getContatos() {
        return contatos;
    }

    public void setContatos(ArrayList<Contact> contatos) {
        this.contatos = contatos;
    }
}
