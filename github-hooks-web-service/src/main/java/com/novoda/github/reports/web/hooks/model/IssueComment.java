package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;

public class IssueComment {

    private GithubIssue githubIssue;
    private GithubComment githubComment;

    public IssueComment(GithubIssue githubIssue, GithubComment githubComment) {
        this.githubIssue = githubIssue;
        this.githubComment = githubComment;
    }

    public GithubIssue getIssue() {
        return githubIssue;
    }

    public GithubComment getComment() {
        return githubComment;
    }
}
