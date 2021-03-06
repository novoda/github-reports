package com.novoda.floatschedule.task;

import com.google.gson.annotations.SerializedName;

public class Task {

    @SerializedName("task_id")
    private int id;

    @SerializedName("task_name")
    private String name;

    @SerializedName("people_id")
    private String personId;

    @SerializedName("person_name")
    private String personName;

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("project_name")
    private String projectName;

    @SerializedName("client_name")
    private String clientName;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    public Task(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Task(String name, String projectName, String personName, String personId) {
        this.name = name;
        this.projectName = projectName;
        this.personName = personName;
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return name + "@" + projectName + " (" + clientName + ") [" + id + "], " + personName + ", from " + startDate + " to " + endDate;
    }
}
