package com.novoda.contributions.floatcom;

import java.util.List;

public class FloatDev {
    private final String name;
    private final List<Task> tasks;

    public FloatDev(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public String getUsername() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return "\nFloatDev{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                '}';
    }

    public static class Task {
        private final String projectName;
        private final String startDate;
        private final String endDate;

        public Task(String projectName, String startDate, String endDate) {
            this.projectName = projectName;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        @Override
        public String toString() {
            return "'\nTask{" +
                    "projectName='" + projectName + '\'' +
                    ", startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    '}';
        }
    }
}
