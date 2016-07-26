package com.novoda.github.reports.web.hooks.extract;

import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommitCommentExtractorTest {

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
        GithubComment comment = new GithubComment();
        given(mockEvent.comment()).willReturn(comment);

        GithubComment actual = extractor.extractFrom(mockEvent);

        assertEquals(comment, actual);
    }

    @Test(expected = ExtractException.class)
    public void givenACommitCommentEvent_whenExtractingThePayload_thenAnExceptionIsThrown() throws Exception {
        given(mockEvent.comment()).willReturn(null);

        extractor.extractFrom(mockEvent);
    }

}
