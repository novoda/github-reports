package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.web.hooks.model.GithubAction;

@Deprecated
public class ActionToEventTypeConverter implements Converter<GithubAction, EventType> {

    // FIXME @RUI we have an issue here: without the rest of the payload there's not enough context to convert to the right EventType
    // @RUI alternative would be to have inteface: Converter<PullRequest, Action, Event>

    @Override
    public EventType convertFrom(GithubAction action) throws ConverterException {

        switch (action) {
            case ADDED:
                //return EventType.ISSUE_LABEL_ADD ?
                //return EventType.PULL_REQUEST_LABEL_ADD ?
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
