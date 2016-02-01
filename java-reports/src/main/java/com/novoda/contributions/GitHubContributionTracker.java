package com.novoda.contributions;

import java.io.IOException;

public class GitHubContributionTracker {

    private final String githubAccessToken;

    public GitHubContributionTracker(String githubAccessToken) {
        this.githubAccessToken = githubAccessToken;
    }

    /**
     * Find out if X developer has commented on / merged / closed another projects PR
     *
     * @return
     * @throws IOException
     */
    public String track(FloatDevs floatDevs) throws IOException {
        // From person X's tasks find all project names


        // Query each github repo


        return "todo work out output format";
    }

}
