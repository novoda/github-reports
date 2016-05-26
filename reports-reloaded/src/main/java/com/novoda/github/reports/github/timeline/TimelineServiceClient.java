package com.novoda.github.reports.github.timeline;

import rx.Observable;
import rx.schedulers.Schedulers;

public class TimelineServiceClient {

    private final TimelineService timelineService;

    public static TimelineServiceClient newInstance() {
        TimelineService timelineService = GithubTimelineService.newInstance();
        return new TimelineServiceClient(timelineService);
    }

    private TimelineServiceClient(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public Observable<Event> getTimelineFor(String organisation, String repository, Integer issueNumber) {
        return timelineService.getTimelineFor(organisation, repository, issueNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate());
    }
}
