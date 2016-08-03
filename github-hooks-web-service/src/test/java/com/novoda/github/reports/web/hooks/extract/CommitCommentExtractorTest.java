package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.CommitComment;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommitCommentExtractorTest {

    private static final long ANY_USER_ID = 86L;
    private static final String ANY_USERNAME = "mateus";
    private static final long ANY_COMMENT_ID = 88L;
    private static final Date ANY_DATE = new Date();
    private static final java.lang.Long ANY_REPOSITORY_ID = 42L;
    private static final String ANY_REPOSITORY_NAME = "rosé";
    private static final boolean ANY_IS_PRIVATE_REPOSITORY = false;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private CommitCommentExtractor extractor;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenACommitCommentEvent_whenExtractingThePayload_thenItIsExtracted() throws Exception {
        CommitComment expected = givenACommitComment();

        CommitComment actual = extractor.extractFrom(mockEvent);

        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private CommitComment givenACommitComment() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID, ANY_USERNAME);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY);
        given(mockEvent.comment()).willReturn(githubComment);
        given(mockEvent.repository()).willReturn(githubRepository);
        given(mockEvent.action()).willReturn(GithubAction.CREATED);
        return new CommitComment(githubComment, githubRepository, GithubAction.CREATED);
    }

    @Test(expected = ExtractException.class)
    public void givenACommitCommentEventWithNoComment_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

    @Test(expected = ExtractException.class)
    public void givenACommitCommentEventWithNoRepository_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.repository()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
