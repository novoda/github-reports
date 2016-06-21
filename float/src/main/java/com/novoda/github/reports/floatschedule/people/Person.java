package com.novoda.github.reports.floatschedule.people;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("people_id")
    private int id;

    private String name;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " [" + id + "]";
    }
}
