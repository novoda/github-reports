package com.novoda.github.reports.web.hooks.model;

public abstract class Event {

    protected GithubAction action;

    public GithubAction getAction() {
        return action;
    }
}
