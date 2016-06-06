package com.novoda.github.reports.batch.pullrequest;

import com.novoda.github.reports.batch.issue.Comment;
import com.novoda.github.reports.batch.network.DateToISO8601Converter;
import com.novoda.github.reports.batch.network.GithubApiService;
import com.novoda.github.reports.batch.network.GithubServiceFactory;
import com.novoda.github.reports.batch.network.PagedTransformer;
import com.novoda.github.reports.batch.network.RateLimitDelayTransformer;

import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class GithubPullRequestService implements PullRequestService {

    private static final int DEFAULT_PER_PAGE_COUNT = 100;
    private static final int FIRST_PAGE = 1;

    private final GithubApiService githubApiService;
    private final DateToISO8601Converter dateConverter;
    private final RateLimitDelayTransformer<Comment> commentRateLimitDelayTransformer;

    public static GithubPullRequestService newInstance() {
        GithubServiceFactory githubServiceFactory = GithubServiceFactory.newInstance();
        DateToISO8601Converter dateConverter = new DateToISO8601Converter();
        RateLimitDelayTransformer<Comment> commentRateLimitDelayTransformer = RateLimitDelayTransformer.newInstance();
        return new GithubPullRequestService(githubServiceFactory.createService(), dateConverter, commentRateLimitDelayTransformer);
    }

    private GithubPullRequestService(GithubApiService githubApiService,
                                     DateToISO8601Converter dateConverter,
                                     RateLimitDelayTransformer<Comment> commentRateLimitDelayTransformer) {

        this.githubApiService = githubApiService;
        this.dateConverter = dateConverter;
        this.commentRateLimitDelayTransformer = commentRateLimitDelayTransformer;
    }

    @Override
    public Observable<Comment> getReviewCommentsForPullRequestFor(String organisation, String repository, Integer pullRequestNumber, Date since) {
        String date = dateConverter.toISO8601NoMillisOrNull(since);
        return getPagedReviewCommentsForPullRequestFor(organisation, repository, pullRequestNumber, date, FIRST_PAGE, DEFAULT_PER_PAGE_COUNT)
                .flatMapIterable(Response::body);
    }

    private Observable<Response<List<Comment>>> getPagedReviewCommentsForPullRequestFor(String organisation,
                                                                                        String repository,
                                                                                        Integer pullRequestNumber,
                                                                                        String since,
                                                                                        Integer page,
                                                                                        Integer pageCount) {

        return githubApiService.getReviewCommentsResponseForPullRequestAndPage(organisation, repository, pullRequestNumber, since, page, pageCount)
                .compose(commentRateLimitDelayTransformer)
                .compose(PagedTransformer.newInstance(nextPage -> getPagedReviewCommentsForPullRequestFor(organisation,
                                                                                                          repository,
                                                                                                          pullRequestNumber,
                                                                                                          since,
                                                                                                          nextPage,
                                                                                                          pageCount)));
    }
}
