package com.novoda.reports.pullrequest.comment;

import com.novoda.reports.pullrequest.LitePullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.util.List;
import java.util.stream.Stream;

public class CommentFinder {

    private final CommentInMemoryDataSource inMemoryDataSource;
    private final CommentPersistenceDataSource persistenceDataSource;
    private final CommentWebServiceDataSource webServiceDataSource;

    public static CommentFinder newInstance(PullRequestService pullRequestService) {
        CommentInMemoryDataSource inMemoryDataSource = new CommentInMemoryDataSource();
        CommentPersistenceDataSource persistenceDataSource = new CommentPersistenceDataSource();
        CommentConverter converter = new CommentConverter();
        CommentWebServiceDataSource webServiceDataSource = new CommentWebServiceDataSource(pullRequestService, converter);
        return new CommentFinder(inMemoryDataSource, persistenceDataSource, webServiceDataSource);
    }

    public CommentFinder(CommentInMemoryDataSource inMemoryDataSource,
                         CommentPersistenceDataSource persistenceDataSource,
                         CommentWebServiceDataSource webServiceDataSource) {
        this.inMemoryDataSource = inMemoryDataSource;
        this.persistenceDataSource = persistenceDataSource;
        this.webServiceDataSource = webServiceDataSource;
    }

    public Stream<Comment> getComments(LitePullRequest pullRequest) {
        List<Comment> inMemoryComments = inMemoryDataSource.readComments(pullRequest);
        if (!inMemoryComments.isEmpty()) {
            return inMemoryComments.stream();
        }
        List<Comment> diskComments = persistenceDataSource.readComments(pullRequest);
        if (!diskComments.isEmpty()) {
            inMemoryDataSource.createComments(pullRequest, diskComments);
            return diskComments.stream();
        }
        List<Comment> comments = webServiceDataSource.readComments(pullRequest);
        persistenceDataSource.createComments(pullRequest, comments);
        inMemoryDataSource.createComments(pullRequest, comments);
        return comments.stream();
    }

}
