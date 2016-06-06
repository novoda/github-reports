package com.novoda.github.reports.batch.issue;

import com.novoda.github.reports.batch.network.DateToISO8601Converter;
import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceFactory;
import com.novoda.github.reports.batch.network.PagedTransformer;
import com.novoda.github.reports.batch.network.RateLimitDelayTransformer;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

class GithubIssueService implements IssueService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final Issue.State DEFAULT_STATE = Issue.State.ALL;
    private static final Date NO_SINCE_DATE = null;

    private final GithubApiService githubApiService;
    private final DateToISO8601Converter dateConverter;
    private final RateLimitDelayTransformer<Issue> issueRateLimitDelayTransformer;
    private final RateLimitDelayTransformer<Event> eventRateLimitDelayTransformer;
    private final RateLimitDelayTransformer<Comment> commentRateLimitDelayTransformer;

    public static IssueService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        RateLimitDelayTransformer<Issue> issueRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitDelayTransformer<Event> eventRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        RateLimitDelayTransformer<Comment> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        return new GithubIssueService(githubServiceFactory.createService(),
                                      dateConverter,
                                      issueRateLimitDelayTransformer,
                                      eventRateLimitDelayTransformer,
                                      commentRateLimitDelayTransformer);
    }

    private GithubIssueService(GithubApiService githubApiService,
                               DateToISO8601Converter dateConverter,
                               RateLimitDelayTransformer<Issue> issueRateLimitDelayTransformer,
                               RateLimitDelayTransformer<Event> eventRateLimitDelayTransformer,
                               RateLimitDelayTransformer<Comment> commentRateLimitDelayTransformer) {
        this.githubApiService = githubApiService;
        this.dateConverter = dateConverter;
        this.issueRateLimitDelayTransformer = issueRateLimitDelayTransformer;
        this.eventRateLimitDelayTransformer = eventRateLimitDelayTransformer;
        this.commentRateLimitDelayTransformer = commentRateLimitDelayTransformer;
    }

    @Override
    public Observable<Issue> getIssuesFor(String organisation, String repository) {
        return getIssuesFor(organisation, repository, NO_SINCE_DATE, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<Issue> getIssuesFor(String organisation, String repository, Date since) {
        return getIssuesFor(organisation, repository, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Issue>>> getIssuesFor(String organisation,
                                                           String repository,
                                                           Date since,
                                                           Integer page,
                                                           Integer pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return githubApiService.getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, date, page, pageCount)
                .compose(issueRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getIssuesFor(organisation, repository, since, nextPage, pageCount)));
    }

    @Override
    public Observable<Event> getEventsFor(String organisation, String repository, Integer issueNumber) {
        return getEventsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<Event> getEventsFor(String organisation, String repository, Integer issueNumber, Date since) {
        return getEventsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(event -> event.getCreatedAt().after(since));
    }

    private Observable<Response<List<Event>>> getEventsFor(String organisation,
                                                           String repository,
                                                           Integer issueNumber,
                                                           Integer page,
                                                           Integer pageCount) {

        return githubApiService.getEventsResponseForIssueAndPage(organisation, repository, issueNumber, page, pageCount)
                .compose(eventRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getEventsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

    @Override
    public Observable<Comment> getCommentsFor(String organisation, String repository, Integer issueNumber) {
        return getCommentsFor(organisation, repository, issueNumber, NO_SINCE_DATE, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<Comment> getCommentsFor(String organisation, String repository, Integer issueNumber, Date since) {
        return getCommentsFor(organisation, repository, issueNumber, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Comment>>> getCommentsFor(String organisation,
                                                               String repository,
                                                               Integer issueNumber,
                                                               Date since,
                                                               Integer page,
                                                               Integer pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return githubApiService.getCommentsResponseForIssueAndPage(organisation, repository, issueNumber, date, page, pageCount)
                .compose(commentRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getCommentsFor(organisation,
                                                                                 repository,
                                                                                 issueNumber,
                                                                                 since,
                                                                                 nextPage,
                                                                                 pageCount)));
    }
}
