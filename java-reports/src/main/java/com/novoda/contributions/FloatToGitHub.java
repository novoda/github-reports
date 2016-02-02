package com.novoda.contributions;

import com.novoda.contributions.floatcom.FloatDev;
import com.novoda.contributions.githubcom.GitHubDev;

import java.util.ArrayList;
import java.util.List;

public class FloatToGitHub {

    private final FloatToGitHubUsername floatToGitHubUsername;
    private final FloatToGitHubProject floatToGitHubProject;

    public FloatToGitHub() {
        floatToGitHubUsername = FloatToGitHubUsername.newInstance();
        floatToGitHubProject = FloatToGitHubProject.newInstance();
    }

    /**
     * convert float usernames to github usernames
     * convert float tasks to github projects (1 task could span multiple projects)
     */
    public List<GitHubDev> lookup(List<FloatDev> floatDevs) {
        List<GitHubDev> gitHubDevs = new ArrayList<>();
        for (FloatDev floatDev : floatDevs) {
            String gitHubUsername = floatToGitHubUsername.lookup(floatDev.getUsername());
            List<GitHubDev.Project> projects = new ArrayList<>();
            for (FloatDev.Task task : floatDev.getTasks()) {
                List<String> githubProjectNames = floatToGitHubProject.lookup(task.getProjectName());
                String startDate = task.getStartDate();
                String endDate = task.getEndDate();
                GitHubDev.Project project = new GitHubDev.Project(githubProjectNames, startDate, endDate);
                projects.add(project);
            }
            gitHubDevs.add(new GitHubDev(gitHubUsername, projects));
        }
        return gitHubDevs;
    }

}
