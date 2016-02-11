package com.novoda.contributions.githubcom;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.pullrequest.comment.Comment;

import java.util.List;

public class GitHubDev {

    private final String username;
    private final List<Project> projects;

    public GitHubDev(String username, List<Project> projects) {
        this.username = username;
        this.projects = projects;
    }

    public String getUsername() {
        return username;
    }

    public boolean wrote(Comment comment) {
        return username.equalsIgnoreCase(comment.getUserLogin());
    }

    public boolean hasNotWorkedOn(OrganisationRepo repo) {
        return !hasWorkedOn(repo);
    }

    public boolean hasWorkedOn(OrganisationRepo repo) {
        for (Project project : projects) {
            for (String repoName : project.repoNames) {
                if (repoName.equalsIgnoreCase(repo.getName())) {
                    return true;
                }
            }
        }
        return false;
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
