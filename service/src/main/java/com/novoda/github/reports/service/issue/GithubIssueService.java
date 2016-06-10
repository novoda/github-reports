package com.novoda.github.reports.service.issue;

import com.novoda.github.reports.service.network.GithubApiService;
import com.novoda.github.reports.service.network.GithubServiceContainer;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

public class GithubIssueService implements IssueService {

    private static final GithubIssue.State DEFAULT_STATE = GithubIssue.State.ALL;

    private final GithubApiService githubApiService;

    public static IssueService newInstance() {
        GithubApiService githubApiService = GithubServiceContainer.getGithubService();
        return new GithubIssueService(githubApiService);
    }

    private GithubIssueService(GithubApiService githubApiService) {
        this.githubApiService = githubApiService;
    }

    @Override
    public Observable<Response<List<GithubIssue>>> getIssuesFor(String organisation,
                                                                String repository,
                                                                GithubIssue.State state,
                                                                String date,
                                                                int page,
                                                                int pageCount) {

        return githubApiService.getIssuesResponseForPage(organisation, repository, DEFAULT_STATE, date, page, pageCount);
    }

    @Override
    public Observable<Response<List<GithubEvent>>> getEventsFor(String organisation, String repository, int issueNumber, int page, int pageCount) {
        return githubApiService.getEventsResponseForIssueAndPage(organisation, repository, issueNumber, page, pageCount);
    }

    @Override
    public Observable<Response<List<GithubComment>>> getCommentsFor(String organisation,
                                                                    String repository,
                                                                    int issueNumber,
                                                                    String since,
                                                                    int page,
                                                                    int pageCount) {

        return githubApiService.getCommentsResponseForIssueAndPage(
                organisation,
                repository,
                issueNumber,
                since,
                page,
                pageCount
        );
    }
}
