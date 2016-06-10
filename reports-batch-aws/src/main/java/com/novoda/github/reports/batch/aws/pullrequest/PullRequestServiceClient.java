package com.novoda.github.reports.batch.aws.pullrequest;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.pullrequest.GithubPullRequestService;
import com.novoda.github.reports.service.pullrequest.PullRequestService;

import java.util.Date;

import retrofit2.Response;
import rx.Observable;

public class PullRequestServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;

    private final PullRequestService pullRequestService;
    private final DateToISO8601Converter dateConverter;

    public static PullRequestServiceClient newInstance() {
        PullRequestService pullRequestService = GithubPullRequestService.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        return new PullRequestServiceClient(pullRequestService, dateConverter);
    }

    private PullRequestServiceClient(PullRequestService pullRequestService, DateToISO8601Converter dateConverter) {
        this.pullRequestService = pullRequestService;
        this.dateConverter = dateConverter;
    }

    public Observable<GithubComment> getPullRequestReviewCommentsFor(String organisation,
                                                                     String repository,
                                                                     Integer pullRequestNumber,
                                                                     Date since,
                                                                     int page) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return pullRequestService.getPullRequestReviewCommentsFor(organisation, repository, pullRequestNumber, date, page, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }
}
