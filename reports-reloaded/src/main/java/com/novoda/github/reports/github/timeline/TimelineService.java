package com.novoda.github.reports.github.timeline;

import rx.Observable;

interface TimelineService {

    Observable<TimelineEvent> getTimelineFor(String organisation, String repository, Integer issueNumber);

}
