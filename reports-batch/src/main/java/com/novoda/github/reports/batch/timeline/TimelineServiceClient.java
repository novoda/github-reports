package com.novoda.github.reports.batch.timeline;

import rx.Observable;

public class TimelineServiceClient {

    private final TimelineService timelineService;

    public static TimelineServiceClient newInstance() {
        TimelineService timelineService = GithubTimelineService.newInstance();
        return new TimelineServiceClient(timelineService);
    }

    private TimelineServiceClient(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public Observable<TimelineEvent> getTimelineFor(String organisation, String repository, Integer issueNumber) {
        return timelineService.getTimelineFor(organisation, repository, issueNumber);
    }
}
