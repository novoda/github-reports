package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.web.hooks.model.GithubAction;

public class ActionToEventTypeConverter implements Converter<GithubAction, EventType> {

    @Override
    public EventType convertFrom(GithubAction action) throws ConverterException {

        switch (action) {
            case ADDED:

                break;
            case CLOSED:

                break;
            case CREATED:

                break;
            case OPENED:

                break;
            case PUBLISHED:

                break;
            default:
                // TODO throw exception?
        }

        return null;
    }
}
