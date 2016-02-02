package com.novoda.contributions.convert;

import com.novoda.contributions.floatcom.FloatDev;
import com.novoda.contributions.githubcom.GitHubDev;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FloatToGitHub {

    private final FloatToGitHubUsername floatToGitHubUsername;
    private final FloatToGitHubProject floatToGitHubProject;

    public static FloatToGitHub newInstance() {
        return new FloatToGitHub(FloatToGitHubUsername.newInstance(), FloatToGitHubProject.newInstance());
    }

    FloatToGitHub(FloatToGitHubUsername floatToGitHubUsername, FloatToGitHubProject floatToGitHubProject) {
        this.floatToGitHubUsername = floatToGitHubUsername;
        this.floatToGitHubProject = floatToGitHubProject;
    }

    /**
     * convert float usernames to github usernames
     * convert float tasks to github projects (1 task could span multiple projects)
     */
    public Stream<GitHubDev> lookup(Stream<FloatDev> floatDevs) {
        return floatDevs.map(floatDev -> {
            String gitHubUsername = floatToGitHubUsername.lookup(floatDev.getUsername());
            List<GitHubDev.Project> projects = floatDev
                    .getTasks()
                    .stream()
                    .map(task -> {
                        List<String> githubProjectNames = floatToGitHubProject.lookup(task.getProjectName());
                        String startDate = task.getStartDate();
                        String endDate = task.getEndDate();
                        return new GitHubDev.Project(githubProjectNames, startDate, endDate);
                    })
                    .collect(Collectors.toList());
            return new GitHubDev(gitHubUsername, projects);
        });
    }

}
