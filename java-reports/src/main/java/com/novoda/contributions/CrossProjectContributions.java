package com.novoda.contributions;

import com.novoda.contributions.convert.FloatToGitHub;
import com.novoda.contributions.floatcom.FloatContributionTracker;
import com.novoda.contributions.floatcom.FloatDev;
import com.novoda.contributions.githubcom.GitHubContributionTracker;
import org.eclipse.egit.github.core.client.GitHubClient;

import java.io.IOException;
import java.util.stream.Stream;

public class CrossProjectContributions {

    public static void main(String[] args) throws IOException {
        String floatAccessToken = args[0];
        String githubAccessToken = args[1];
        // Input needed - Date Range
        String startDate = "2016-01-11";
        String endDate = "2016-01-18";
        FloatContributionTracker floatContributionTracker = new FloatContributionTracker(floatAccessToken);
        FloatToGitHub floatToGitHub = FloatToGitHub.newInstance();
        Stream<FloatDev> floatDevs = floatContributionTracker.track(startDate, endDate);
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(githubAccessToken);
        GitHubContributionTracker contributionTracker = GitHubContributionTracker.newInstance(client, "Novoda");
        floatToGitHub.lookup(floatDevs)
                .forEach(contributionTracker::track);
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