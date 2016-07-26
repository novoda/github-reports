package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;

public class ReviewComment {

    private GithubIssue issue;
    private GithubComment comment;

    public ReviewComment(GithubIssue issue, GithubComment comment) {
        this.issue = issue;
        this.comment = comment;
    }

    public GithubIssue getIssue() {
        return issue;
    }

    public GithubComment getComment() {
        return comment;
    }
}
