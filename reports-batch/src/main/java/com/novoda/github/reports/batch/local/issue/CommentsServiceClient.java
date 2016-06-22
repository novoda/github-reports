package com.novoda.github.reports.batch.local.issue;

import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubject;
import com.novoda.github.reports.batch.local.retry.RateLimitResetTimerSubjectContainer;
import com.novoda.github.reports.batch.local.retry.RetryWhenTokenResets;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssueService;
import com.novoda.github.reports.service.issue.IssueService;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.network.DateToISO8601Converter;
import com.novoda.github.reports.service.network.PagedTransformer;
import com.novoda.github.reports.service.network.RateLimitDelayTransformer;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class CommentsServiceClient {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final IssueService issueService;
    private final ReviewCommentsServiceClient reviewCommentsServiceClient;
    private final DateToISO8601Converter dateConverter;
    private final RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer;

    private final RateLimitResetTimerSubject rateLimitResetTimerSubject;
    private final RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer;

    public static CommentsServiceClient newInstance() {
        IssueService issueService = GithubIssueService.newCachingInstance();
        ReviewCommentsServiceClient reviewCommentsServiceClient = ReviewCommentsServiceClient.newInstance();

        DateToISO8601Converter dateConverter = new DateToISO8601Converter();

        RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer = RepositoryIssueEventPersistTransformer.newInstance();

        RateLimitResetTimerSubject rateLimitResetTimerSubject = RateLimitResetTimerSubjectContainer.getInstance();
        RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();

        return new CommentsServiceClient(issueService,
                                         reviewCommentsServiceClient,
                                         dateConverter,
                                         repositoryIssueEventPersistTransformer,
                                         rateLimitResetTimerSubject,
                                         commentRateLimitDelayTransformer);
    }

    private CommentsServiceClient(IssueService issueService,
                                  ReviewCommentsServiceClient reviewCommentsServiceClient,
                                  DateToISO8601Converter dateConverter,
                                  RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer,
                                  RateLimitResetTimerSubject rateLimitResetTimerSubject,
                                  RateLimitDelayTransformer<GithubComment> commentRateLimitDelayTransformer) {

        this.issueService = issueService;
        this.reviewCommentsServiceClient = reviewCommentsServiceClient;
        this.dateConverter = dateConverter;
        this.repositoryIssueEventPersistTransformer = repositoryIssueEventPersistTransformer;
        this.rateLimitResetTimerSubject = rateLimitResetTimerSubject;
        this.commentRateLimitDelayTransformer = commentRateLimitDelayTransformer;
    }

    public Observable<RepositoryIssueEvent> retrieveCommentsAsEventsFrom(RepositoryIssue repositoryIssue, Date since) {
        return Observable.merge(retrieveCommentsFromIssue(repositoryIssue, since),
                                reviewCommentsServiceClient.retrieveReviewCommentsFromPullRequest(repositoryIssue, since))
                .map(comment -> new RepositoryIssueEventComment(repositoryIssue, comment))
                .compose(repositoryIssueEventPersistTransformer);
    }

    private Observable<GithubComment> retrieveCommentsFromIssue(RepositoryIssue repositoryIssue, Date since) {
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return getPagedCommentsFor(organisation, repository, issueNumber, since, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body)
                .compose(RetryWhenTokenResets.newInstance(rateLimitResetTimerSubject));
    }

    private Observable<Response<List<GithubComment>>> getPagedCommentsFor(String organisation,
                                                                          String repository,
                                                                          int issueNumber,
                                                                          Date since,
                                                                          int page,
                                                                          int pageCount) {

        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return issueService.getCommentsFor(organisation, repository, issueNumber, date, page, pageCount)
                .compose(commentRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedCommentsFor(
                        organisation,
                        repository,
                        issueNumber,
                        since,
                        nextPage,
                        pageCount
                )));
    }

}
