package com.novoda.contributions;

import java.util.List;

public class FloatDevs {
    private final String name;
    private final List<Task> tasks;

    public FloatDevs(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "\nFloatDevs{" +
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
