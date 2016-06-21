package com.novoda.github.reports.floatschedule.task;

import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("task_id")
    private int id;

    @SerializedName("task_name")
    private String name;

    @SerializedName("person_name")
    private String personName;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

}
