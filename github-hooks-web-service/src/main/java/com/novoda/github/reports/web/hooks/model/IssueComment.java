package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;

public class IssueComment extends Event {

    private GithubIssue githubIssue;
    private GithubComment githubComment;

    public IssueComment(GithubIssue githubIssue, GithubComment githubComment, GithubAction action) {
        this.githubIssue = githubIssue;
        this.githubComment = githubComment;
        this.action = action;
    }

    public GithubIssue getIssue() {
        return githubIssue;
    }

    public GithubComment getComment() {
        return githubComment;
    }
}
