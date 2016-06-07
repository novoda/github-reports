package com.novoda.github.reports.batch.issue;

import rx.Observable;

class ReviewCommentsTransformer implements Observable.Transformer<Comment, Comment> {

    private final RepositoryIssue repositoryIssue;
    private final ReviewCommentsMerger reviewCommentsMerger;

    static ReviewCommentsTransformer newInstance(RepositoryIssue repositoryIssue, ReviewCommentsMerger reviewCommentsMerger) {
        return new ReviewCommentsTransformer(repositoryIssue, reviewCommentsMerger);
    }

    ReviewCommentsTransformer(RepositoryIssue repositoryIssue, ReviewCommentsMerger reviewCommentsMerger) {
        this.repositoryIssue = repositoryIssue;
        this.reviewCommentsMerger = reviewCommentsMerger;
    }

    @Override
    public Observable<Comment> call(Observable<Comment> observable) {
        if (repositoryIssue.getIssue().isPullRequest()) {
            return observable.mergeWith(reviewCommentsMerger.merge());
        }
        return observable;
    }

    @FunctionalInterface
    interface ReviewCommentsMerger {
        Observable<Comment> merge();
    }
}
