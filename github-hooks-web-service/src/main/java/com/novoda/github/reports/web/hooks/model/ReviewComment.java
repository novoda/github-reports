package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;

public class ReviewComment extends Event {

    private GithubComment comment;
    private GithubRepository repository;

    public ReviewComment(GithubComment comment, GithubRepository repository, GithubAction action) {
        this.comment = comment;
        this.repository = repository;
        this.action = action;
    }

    public GithubComment getComment() {
        return comment;
    }

    public GithubRepository getRepository() {
        return repository;
    }
}
