package com.novoda.github.reports.floatschedule.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Assignment {

    @SerializedName("people_id")
    private int personId;

    @SerializedName("tasks")
    private List<Task> tasks;

    Assignment(int personId, List<Task> tasks) {
        this.personId = personId;
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public int getPersonId() {
        return personId;
    }
}
