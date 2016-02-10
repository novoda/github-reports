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

    @Override
    public String toString() {
        return "LitePullRequest{" +
                "repoName='" + repoName + '\'' +
                ", repoOwnerLogin='" + repoOwnerLogin + '\'' +
                ", number=" + number +
                ", title='" + title + '\'' +
                ", userLogin='" + userLogin + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LitePullRequest that = (LitePullRequest) o;

        return number == that.number &&
                repoName.equals(that.repoName) &&
                repoOwnerLogin.equals(that.repoOwnerLogin) &&
                title.equals(that.title) &&
                userLogin.equals(that.userLogin) &&
                createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        int result = repoName.hashCode();
        result = 31 * result + repoOwnerLogin.hashCode();
        result = 31 * result + number;
        result = 31 * result + title.hashCode();
        result = 31 * result + userLogin.hashCode();
        result = 31 * result + createdAt.hashCode();
        return result;
    }
}
