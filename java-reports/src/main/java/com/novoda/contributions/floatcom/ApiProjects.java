package com.novoda.contributions.floatcom;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class ApiProjects {

    @SerializedName("projects")
    public List<ApiProject> projects;

    public static class ApiProject {

        @SerializedName("project_id")
        public String projectId;

        @SerializedName("project_name")
        public String projectName;

        @SerializedName("client_id")
        public String clientId;

        @SerializedName("client_name")
        public String clientName;

        @Override
        public String toString() {
            return "ApiProject{" +
                    "projectId='" + projectId + '\'' +
                    ", projectName='" + projectName + '\'' +
                    ", clientId='" + clientId + '\'' +
                    ", clientName='" + clientName + '\'' +
                    "}\n";
        }
    }

    @Override
    public String toString() {
        return "ApiProjects{" +
                "projects=" + projects +
                '}';
    }
}
