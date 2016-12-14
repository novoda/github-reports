package com.novoda.floatschedule.convert;

import java.util.Map;
import java.util.function.Predicate;

class GithubUsernameFilter implements Predicate<Map.Entry<String, String>> {

    private final String githubUsername;

    static GithubUsernameFilter byGithubUsername(String githubUsername) {
        return new GithubUsernameFilter(githubUsername);
    }

    private GithubUsernameFilter(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    @Override
    public boolean test(Map.Entry<String, String> floatNameToGithubUserName) {
        return floatNameToGithubUserName.getValue().equalsIgnoreCase(githubUsername);
    }

}
