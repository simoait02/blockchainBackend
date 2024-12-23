package com.aseds.aithssainesbaiti.domain;


public class IdGenerator {
    static int id;
    public static int getId(){
        return ++id;
    }
}
