package com.aseds.aithssainesbaiti.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
public class User {
    @Getter
     int id;
    @Getter
    String name;
    @Getter
    double sold;
    public User(String name,double sold) {
        id=IdGenerator.getId();
        this.name = name;
        this.sold = sold;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSold() {
        return sold;
    }

    public void setSold(double sold) {
        this.sold = sold;
    }
}
