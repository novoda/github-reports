package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.web.hooks.model.GithubAction;

public interface EventConverter<From, To> extends Converter<From, To> {

    EventType convertAction(GithubAction action) throws UnsupportedActionException;

}
