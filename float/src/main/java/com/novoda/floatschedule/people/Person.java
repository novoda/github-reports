package com.novoda.floatschedule.people;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("people_id")
    private int id;

    private String name;

    Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " [" + id + "]";
    }
}
