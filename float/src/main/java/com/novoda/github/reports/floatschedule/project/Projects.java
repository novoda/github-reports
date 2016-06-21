package com.novoda.github.reports.floatschedule.project;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Projects {

    @SerializedName("projects")
    private List<Project> projects;

    Projects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Project> getProjects() {
        return projects;
    }
}
