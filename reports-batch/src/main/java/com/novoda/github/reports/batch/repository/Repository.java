package com.novoda.github.reports.batch.repository;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.batch.User;

public class Repository {

    private Long id;

    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("private")
    private boolean privateRepo;

    @SerializedName("forked")
    private boolean forkedRepo;

    private User owner;

    @SerializedName("open_issues_count")
    private int openIssues;

    @SerializedName("has_issues")
    private boolean issuesPresent;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isPrivateRepo() {
        return privateRepo;
    }

    public boolean isForkedRepo() {
        return forkedRepo;
    }

    public User getOwner() {
        return owner;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public boolean issuesPresent() {
        return issuesPresent;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
