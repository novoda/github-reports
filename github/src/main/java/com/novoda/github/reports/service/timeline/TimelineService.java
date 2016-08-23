package com.novoda.github.reports.service.timeline;

import rx.Observable;


/**
 * If you want to use this, you shall change the {@link com.novoda.github.reports.service.network.CustomMediaTypeInterceptor}
 * and make it inject the "Accept" header with value "application/vnd.github.mockingbird-preview".
 *
 * Github APIs currently does no support multiple preview types as "Accept" values, and we need that value to be set
 * for the Reactions endpoint.
 */
@Deprecated
public interface TimelineService {

    Observable<TimelineEvent> getTimelineFor(String organisation, String repository, int issueNumber);

}
