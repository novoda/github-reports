package com.novoda.github.reports.batch.timeline;

import rx.Observable;

interface TimelineService {

    Observable<TimelineEvent> getTimelineFor(String organisation, String repository, Integer issueNumber);

}
