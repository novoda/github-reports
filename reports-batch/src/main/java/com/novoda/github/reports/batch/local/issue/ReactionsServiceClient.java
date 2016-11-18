package com.novoda.github.reports.batch.local.issue;

import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.local.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubReaction;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventReaction;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

public class ReactionsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final GithubApiService apiService;

    private final RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer;
    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;
    private final RateLimitDelayTransformer<GithubReaction> reactionRateLimitDelayTransformer;

    public static ReactionsServiceClient newInstance() {
        GithubApiService apiService = GithubCachingServiceContainer.getGithubService();
        RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer = RepositoryIssueEventPersistTransformer.newInstance();
        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        RateLimitDelayTransformer<GithubReaction> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();

        return new ReactionsServiceClient(apiService,
                                          repositoryIssueEventPersistTransformer,
                                          rateLimitResetTimerSubject,
                                          commentRateLimitDelayTransformer);
    }

    private ReactionsServiceClient(GithubApiService apiService,
                                   RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer,
                                   RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                   RateLimitDelayTransformer<GithubReaction> reactionRateLimitDelayTransformer) {

        this.apiService = apiService;
        this.repositoryIssueEventPersistTransformer = repositoryIssueEventPersistTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.reactionRateLimitDelayTransformer = reactionRateLimitDelayTransformer;
    }

    public Observable<RepositoryIssueEvent> retrieveReactionsAsEventsFrom(RepositoryIssue repositoryIssue, Date since) {
        return retrieveReactionsFromIssue(repositoryIssue, since)
                .map(reaction -> new RepositoryIssueEventReaction(repositoryIssue, reaction))
                .compose(repositoryIssueEventPersistTransformer);
    }

    private Observable<GithubReaction> retrieveReactionsFromIssue(RepositoryIssue repositoryIssue, Date since) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return getPagedReactionsFor(organisation, repository, issueNumber, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .filter(onlyCreatedAfter(since))
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private Observable<Response<List<GithubReaction>>> getPagedReactionsFor(String organisation,
                                                                            String repository,
                                                                            int issueNumber,
                                                                            int page,
                                                                            int pageCount) {

        return apiService.getReactionsForIssueAndPage(organisation, repository, issueNumber, page, pageCount)
                .compose(reactionRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedReactionsFor(
                        organisation,
                        repository,
                        issueNumber,
                        nextPage,
                        pageCount
                )));
    }

    private Func1<GithubReaction, Boolean> onlyCreatedAfter(Date since) {
        return event -> since == null || event.getCreatedAt().after(since);
    }
}
