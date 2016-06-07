package com.novoda.github.reports.batch.issue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReviewCommentsTransformerTest {

    private static final Observable<Comment> ANY_COMMENT_OBSERVABLE = Observable.empty();

    @Mock
    RepositoryIssue mockRepositoryIssue;

    @Mock
    Issue mockIssue;

    @Mock
    ReviewCommentsTransformer.ReviewCommentsMerger mockReviewCommentsMerger;

    private ReviewCommentsTransformer reviewCommentsTransformer;

    @Before
    public void setUp() {
        initMocks(this);
        reviewCommentsTransformer = new ReviewCommentsTransformer(mockRepositoryIssue, mockReviewCommentsMerger);
        when(mockRepositoryIssue.getIssue()).thenReturn(mockIssue);
    }

    @Test
    public void givenARepositoryWithAPullRequestIssue_whenTransforming_thenMergeReviewComments() {
        when(mockIssue.isPullRequest()).thenReturn(true);

        reviewCommentsTransformer.call(ANY_COMMENT_OBSERVABLE);

        verify(mockReviewCommentsMerger).merge();
    }

    @Test
    public void givenARepositoryWithARegularIssue_whenTransforming_thenDoNotMergeReviewComments() {
        when(mockIssue.isPullRequest()).thenReturn(false);

        reviewCommentsTransformer.call(ANY_COMMENT_OBSERVABLE);

        verifyZeroInteractions(mockReviewCommentsMerger);
    }
}
