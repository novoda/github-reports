package com.novoda.reports.pullrequest;

import java.time.LocalDate;

public class LitePullRequest {

    private final String repoName;
    private final String repoOwnerLogin;
    private final int number;
    private final String title;
    private final String userLogin;
    private final LocalDate createdAt;

    public LitePullRequest(String repoName, String repoOwnerLogin,
                           int number, String title,
                           String userLogin,
                           LocalDate createdAt) {
        this.repoName = repoName;
        this.repoOwnerLogin = repoOwnerLogin;
        this.number = number;
        this.title = title;
        this.userLogin = userLogin;
        this.createdAt = createdAt;
    }

    public String getRepoName() {
        return repoName;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String generateId() {
        return repoOwnerLogin + "/" + repoName;
    }

}
