package com.novoda.github.reports.github.issue;

import com.novoda.github.reports.github.PagedTransformer;
import com.novoda.github.reports.github.network.GithubApiService;
import com.novoda.github.reports.github.network.GithubServiceFactory;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import retrofit2.Response;
import rx.Observable;

class GithubIssueService implements IssueService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;
    private static final Issue.State DEFAULT_STATE = Issue.State.ALL;
    private static final String NO_SINCE_DATE = null;

    private final GithubApiService githubApiService;

    public static IssueService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        return new GithubIssueService(githubServiceFactory.createService());
    }

    private GithubIssueService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository) {
        return getPagedIssuesFor(organisation, repository, NO_SINCE_DATE, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Issue>>> getPagedIssuesFor(String organisation,
                                                                String repository,
                                                                String since,
                                                                Integer page,
                                                                Integer pageCount) {

        return githubApiService.getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, since, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedIssuesFor(organisation, repository, since, nextPage, pageCount)));
    }

    @Override
    public Observable<Issue> getPagedIssuesFor(String organisation, String repository, Date since) {
        String date = new DateTime(since.getTime()).toString();
        return getPagedIssuesFor(organisation, repository, date, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    @Override
    public Observable<Event> getPagedEventsFor(String organisation, String repository, Integer issueNumber) {
        return getPagedEventsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Event>>> getPagedEventsFor(String organisation,
                                                                String repository,
                                                                Integer issueNumber,
                                                                Integer page,
                                                                Integer pageCount) {

        return githubApiService.getEventsResponseForIssueAndPage(organisation, repository, issueNumber, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedEventsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }

    @Override
    public Observable<Comment> getPagedCommentsFor(String organisation, String repository, Integer issueNumber) {
        return getPagedCommentsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Comment>>> getPagedCommentsFor(String organisation,
                                                                    String repository,
                                                                    Integer issueNumber,
                                                                    Integer page,
                                                                    Integer pageCount) {

        return githubApiService.getCommentsResponseForIssueAndPage(organisation, repository, issueNumber, page, pageCount)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedCommentsFor(organisation, repository, issueNumber, nextPage, pageCount)));
    }
}
