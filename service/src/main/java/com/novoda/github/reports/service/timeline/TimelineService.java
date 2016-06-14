package com.novoda.github.reports.service.timeline;

import rx.Observable;

public interface TimelineService {

    Observable<TimelineEvent> getTimelineFor(String organisation, String repository, int issueNumber);

}
