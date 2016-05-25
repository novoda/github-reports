package com.novoda.github.reports.github.timeline;

import rx.Observable;

public interface TimelineService {

    Observable<Event> getTimelineFor(String organisation, String repository, Integer issueNumber);

}
