package com.novoda.github.reports.floatschedule.people;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class People {

    @SerializedName("people")
    private List<Person> persons;

    People(List<Person> persons) {
        this.persons = persons;
    }

    public List<Person> getPersons() {
        return persons;
    }
}
