package com.novoda.github.reports.floatschedule.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Assignment {

    @SerializedName("people_id")
    private int personId;

    private List<Task> tasks;

    public List<Task> getTasks() {
        return tasks;
    }
}
