package com.novoda.github.reports.batch.aws.issue;

import com.novoda.github.reports.batch.aws.pullrequest.PullRequestServiceClient;
import com.novoda.github.reports.service.issue.RepositoryIssue;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.issue.RepositoryIssueEventComment;
import com.novoda.github.reports.service.persistence.RepositoryIssueEventPersistTransformer;

import java.util.Date;

import rx.Observable;

public class ReviewCommentsServiceClient {

    private final PullRequestServiceClient pullRequestServiceClient;
    private final RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer;

    public static ReviewCommentsServiceClient newInstance() {
        PullRequestServiceClient pullRequestServiceClient = PullRequestServiceClient.newInstance();
        RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer = RepositoryIssueEventPersistTransformer.newInstance();
        return new ReviewCommentsServiceClient(pullRequestServiceClient, repositoryIssueEventPersistTransformer);
    }

    private ReviewCommentsServiceClient(PullRequestServiceClient pullRequestServiceClient,
                                        RepositoryIssueEventPersistTransformer repositoryIssueEventPersistTransformer) {

        this.pullRequestServiceClient = pullRequestServiceClient;
        this.repositoryIssueEventPersistTransformer = repositoryIssueEventPersistTransformer;
    }

    public Observable<RepositoryIssueEvent> retrieveReviewCommentsFromPullRequest(RepositoryIssue repositoryIssue, Date since, int page) {
        if (isNotPullRequest(repositoryIssue)) {
            return Observable.empty();
        }
        String organisation = repositoryIssue.getOwnerUsername();
        String repository = repositoryIssue.getRepositoryName();
        int issueNumber = repositoryIssue.getIssueNumber();
        return pullRequestServiceClient.getPullRequestReviewCommentsFor(organisation, repository, issueNumber, since, page)
                .map(comment -> new RepositoryIssueEventComment(repositoryIssue, comment))
                .compose(repositoryIssueEventPersistTransformer);
    }

    private boolean isNotPullRequest(RepositoryIssue repositoryIssue) {
        return !repositoryIssue.isPullRequest();
    }

}
