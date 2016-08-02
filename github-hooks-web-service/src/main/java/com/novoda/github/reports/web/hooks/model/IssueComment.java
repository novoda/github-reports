package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

public class IssueComment extends Event {

    private GithubComment githubComment;
    private GithubRepository githubRepository;
    private GithubIssue githubIssue;

    public IssueComment(GithubComment githubComment, GithubRepository githubRepository, GithubIssue githubIssue, GithubAction action) {
        this.githubIssue = githubIssue;
        this.githubRepository = githubRepository;
        this.githubComment = githubComment;
        this.action = action;
    }

    public GithubIssue getIssue() {
        return githubIssue;
    }

    public GithubRepository getRepository() {
        return githubRepository;
    }

    public GithubComment getComment() {
        return githubComment;
    }
}
