package com.novoda.floatschedule.people;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class People {

    @SerializedName("people")
    private List<Person> persons;

    People(List<Person> persons) {
        this.persons = persons;
    }

    List<Person> getPersons() {
        return persons;
    }
}
