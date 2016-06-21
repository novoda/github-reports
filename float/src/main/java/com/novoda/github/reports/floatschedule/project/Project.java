package com.novoda.github.reports.floatschedule.project;

import com.google.gson.annotations.SerializedName;

public class Project {

    @SerializedName("project_id")
    private int projectId;

    @SerializedName("project_name")
    private String projectName;

    Project(int projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return projectName + " [" + projectId + "]";
    }
}
