package com.novoda.github.reports.web.hooks.model;

import com.novoda.github.reports.service.GithubUser;

public abstract class Event {

    protected GithubAction action;

    protected GithubUser sender;

    public GithubAction getAction() {
        return action;
    }

    public GithubUser getSender() {
        return sender;
    }
}
