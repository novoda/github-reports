package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.web.hooks.model.Event;
import com.novoda.github.reports.web.hooks.model.GithubAction;

public interface EventConverter<From extends Event> extends Converter<From, com.novoda.github.reports.data.model.Event> {

    EventType convertAction(GithubAction action) throws UnsupportedActionException;

}
