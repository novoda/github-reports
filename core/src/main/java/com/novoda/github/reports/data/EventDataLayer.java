package com.novoda.github.reports.data;

import com.novoda.github.reports.data.model.Event;

public interface EventDataLayer {

    Event updateOrInsert(Event event) throws DataLayerException;

}
