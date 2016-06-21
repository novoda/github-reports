package com.novoda.github.reports.floatschedule.project;

import com.google.gson.annotations.SerializedName;

public class Project {

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("project_name")
    private String projectName;

    @Override
    public String toString() {
        return projectName + " [" + projectId + "]";
    }
}
