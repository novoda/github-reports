package com.novoda.contributions.floatcom;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ApiTasks {

    @SerializedName("start_doy")
    int startDayOfYear;

    @SerializedName("start_yr")
    int startYear;

    List<ApiPeopleWithTasks> people;

    @Override
    public String toString() {
        return "ApiTasks{" +
                "startDayOfYear=" + startDayOfYear +
                ", startYear=" + startYear +
                ", people=" + people +
                '}';
    }

    public static class ApiPeopleWithTasks {

        @SerializedName("people_id")
        String personId;

        @SerializedName("tasks")
        List<ApiTask> tasks;

        @Override
        public String toString() {
            return "ApiPeopleWithTasks{\n" +
                    "personId='" + personId + '\'' +
                    ", tasks=" + tasks +
                    "}\n\n";
        }
    }

    public static class ApiTask {

        @SerializedName("start_date")
        String startDate;

        @SerializedName("end_date")
        String endDate;

        @SerializedName("project_name")
        String projectName;

        @Override
        public String toString() {
            return "ApiTask{" +
                    "startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    ", projectName='" + projectName + '\'' +
                    "}\n";
        }
    }

}
