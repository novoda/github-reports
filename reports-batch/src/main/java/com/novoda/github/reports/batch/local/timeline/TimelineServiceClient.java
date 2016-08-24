package com.novoda.github.reports.batch.local.timeline;

import com.novoda.github.reports.service.timeline.GithubTimelineService;
import com.novoda.github.reports.service.timeline.TimelineEvent;
import com.novoda.github.reports.service.timeline.TimelineService;

import rx.Observable;

/**
 * @see TimelineService
 */
@Deprecated
public class TimelineServiceClient {

    private final TimelineService timelineService;

    public static TimelineServiceClient newInstance() {
        TimelineService timelineService = GithubTimelineService.newInstance();
        return new TimelineServiceClient(timelineService);
    }

    private TimelineServiceClient(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    public Observable<TimelineEvent> getTimelineFor(String organisation, String repository, int issueNumber) {
        return timelineService.getTimelineFor(organisation, repository, issueNumber);
    }
}
