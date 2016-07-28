package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.Issue;

public class IssueConverter implements EventConverter<Issue> {

    @Override
    public Event convertFrom(Issue issue) throws ConverterException {
        return null; // TODO
    }

    @Override
    public EventType convertAction(GithubAction action) throws UnsupportedActionException {
        return null; // TODO
    }
}
