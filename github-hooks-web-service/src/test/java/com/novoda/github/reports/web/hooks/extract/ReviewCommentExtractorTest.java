package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.ReviewComment;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReviewCommentExtractorTest {

    private static final long ANY_OWNER_ID = 88;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final GithubAction ANY_ACTION = GithubAction.OPENED;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private ReviewCommentExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAReviewCommentEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        ReviewComment expected = givenAReviewComment();

        ReviewComment actual = extractor.extractFrom(mockEvent);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private ReviewComment givenAReviewComment() {
        GithubUser githubUser = new GithubUser(ANY_OWNER_ID);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        given(mockEvent.comment()).willReturn(githubComment);
        given(mockEvent.repository()).willReturn(githubRepository);
        given(mockEvent.action()).willReturn(ANY_ACTION);
        return new ReviewComment(githubComment, githubRepository, ANY_ACTION);
    }

    @Test(expected = ExtractException.class)
    public void givenAReviewCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.issue()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
