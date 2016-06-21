package com.novoda.github.reports.floatschedule.task;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Assignments {

    @SerializedName("people")
    private List<Assignment> assignments;

    @SerializedName("start_doy")
    private int dayOfYear;

    @SerializedName("start_yr")
    private int year;

    public List<Assignment> getAssignments() {
        return assignments;
    }
}
