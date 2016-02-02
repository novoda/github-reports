package com.novoda.contributions;

import com.novoda.contributions.floatcom.FloatContributionTracker;
import com.novoda.contributions.floatcom.FloatDev;
import com.novoda.contributions.githubcom.GitHubDev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/floatschedule/api
 */
public class CrossProjectContributions {

    public static void main(String[] args) throws IOException {
        String floatAccessToken = args[0];
        // Input needed - Date Range
        FloatContributionTracker floatContributionTracker = new FloatContributionTracker(floatAccessToken);
        List<FloatDev> floatDevs = floatContributionTracker.track("2016-01-11", "2016-01-18");


        // convert the float usernames to github usernames
        // convert the float projects to githubprojects
        FloatToGitHubUsername floatToGitHubUsername = FloatToGitHubUsername.newInstance();
        FloatToGitHubProject floatToGitHubProject = FloatToGitHubProject.newInstance();
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
        // query github with this info

        System.out.println("Complete " + gitHubDevs);
    }


}

//October
//Ataul  The Times (1/10/16 - 12/10/16) , All4 (13/10/16 - 15/10/16), The Times (15/10/16 , 31/10/16)
//Paul    All4 (1/10/16 - 31/10/16)
//
//October
//The Times, All4, Oddschecker
//
//Paul has commented on?  The times(1/10/16 - 31/10/16) / Oddschecker(1/10/16 - 31/10/16)
//Ataul has commented on? , Oddschecker(1/10/16 - 31/10/16), All4 (1/10/16 - 12/10/16), The Times (13/10/16 - 15/10/16), All4 (15/10/16 , 31/10/16)