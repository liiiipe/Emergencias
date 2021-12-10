package com.example.emergencias.model;

import java.io.Serializable;

public class Contact implements Serializable, Comparable  {

    String name;
    String number;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int compareTo(Object o) {
        Contact c2= (Contact)o;
        return this.getName().compareTo(c2.getName());
    }
}
