package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;

public class ReviewComment {

    private GithubWebhookPullRequest issue;
    private GithubComment comment;

    public ReviewComment(GithubWebhookPullRequest issue, GithubComment comment) {
        this.issue = issue;
        this.comment = comment;
    }

    public GithubWebhookPullRequest getIssue() {
        return issue;
    }

    public GithubComment getComment() {
        return comment;
    }
}
