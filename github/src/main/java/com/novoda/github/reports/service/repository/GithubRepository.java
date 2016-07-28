package com.novoda.github.reports.service.repository;

import com.google.gson.annotations.SerializedName;
import com.novoda.github.reports.service.GithubUser;

public class GithubRepository {

    private Long id;

    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("private")
    private boolean privateRepo;

    @SerializedName("forked")
    private boolean forkedRepo;

    private GithubUser owner;

    @SerializedName("open_issues_count")
    private int openIssues;

    @SerializedName("has_issues")
    private boolean issuesPresent;

    public GithubRepository(Long id) {
        this.id = id;
    }

    public GithubRepository(Long id, String name, boolean issuesPresent) {
        this.id = id;
        this.name = name;
        this.issuesPresent = issuesPresent;
    }

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

    public GithubUser getOwner() {
        return owner;
    }

    public String getOwnerUsername() {
        return owner.getUsername();
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public boolean issuesPresent() {
        return issuesPresent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(GithubUser owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
