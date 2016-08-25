package com.novoda.github.reports.service.pullrequest;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubCachingServiceContainer;
import com.novoda.github.reports.service.network.GithubServiceContainer;
import retrofit2.Response;
import rx.Observable;

import java.util.List;

public class GithubPullRequestService implements PullRequestService {

    private final GithubApiService githubApiService;

    public static GithubPullRequestService newInstance(GithubApiService githubApiService) {
        return new GithubPullRequestService(githubApiService);
    }

    public static GithubPullRequestService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        return new GithubPullRequestService(githubApiService);
    }

    public static GithubPullRequestService newCachingInstance() {
        GithubApiService githubApiService = GithubCachingServiceContainer.getGithubService();
        return new GithubPullRequestService(githubApiService);
    }

    private GithubPullRequestService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    public Observable<Response<List<GithubComment>>> getPullRequestReviewCommentsFor(String organisation,
                                                                                     String repository,
                                                                                     int pullRequestNumber,
                                                                                     String since,
                                                                                     int page,
                                                                                     int pageCount) {

        return githubApiService.getReviewCommentsResponseForPullRequestAndPage(organisation, repository, pullRequestNumber, since, page, pageCount);
    }

}
