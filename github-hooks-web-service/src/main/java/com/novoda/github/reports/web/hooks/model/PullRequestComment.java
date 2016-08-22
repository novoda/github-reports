package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.repository.GithubRepository;

public class PullRequestComment extends IssueComment {

    public PullRequestComment(GithubComment githubComment,
                              GithubRepository githubRepository,
                              GithubIssue githubIssue,
                              GithubAction action) {

        super(githubComment, githubRepository, githubIssue, action);
    }
}
