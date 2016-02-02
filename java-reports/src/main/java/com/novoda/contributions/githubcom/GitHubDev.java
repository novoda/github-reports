package com.novoda.contributions.githubcom;

import java.util.List;
import java.util.stream.Stream;

public class GitHubDev {

    private final String username;
    private final List<Project> projects;

    public GitHubDev(String username, List<Project> projects) {
        this.username = username;
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "\nGitHubDev{" +
                "username='" + username + '\'' +
                ", projects=" + projects +
                '}';
    }

    public static class Project {
        private final List<String> repoNames;
        private final String startDate;
        private final String endDate;

        public Project(List<String> repoNames, String startDate, String endDate) {
            this.repoNames = repoNames;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return "'\nProject{" +
                    "repoNames='" + repoNames + '\'' +
                    ", startDate='" + startDate + '\'' +
                    ", endDate='" + endDate + '\'' +
                    '}';
        }
    }
}
