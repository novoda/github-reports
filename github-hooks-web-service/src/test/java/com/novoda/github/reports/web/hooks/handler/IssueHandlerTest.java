package com.novoda.github.reports.web.hooks.handler;

import com.novoda.github.reports.web.hooks.classification.EventType;
import com.novoda.github.reports.web.hooks.extract.ExtractException;
import com.novoda.github.reports.web.hooks.extract.IssueExtractor;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.Issue;
import com.novoda.github.reports.web.hooks.persistence.IssuePersister;
import com.novoda.github.reports.web.hooks.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssueHandlerTest {

    @Mock
    private IssueExtractor mockExtractor;

    @Mock
    private IssuePersister mockPersister;

    @Mock
    private GithubWebhookEvent mockEvent;

    @InjectMocks
    private IssueHandler issueHandler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAnEvent_whenHandlingIt_thenWeDelegateToExtractor() throws ExtractException, UnhandledEventException {

        issueHandler.handle(mockEvent);

        verify(mockExtractor).extractFrom(mockEvent);
    }

    @Test
    public void givenAnEvent_whenHandlingIt_thenWeDelegateToPersister() throws UnhandledEventException, PersistenceException {

        issueHandler.handle(mockEvent);

        verify(mockPersister).persist(any(Issue.class));
    }


    @Test(expected = UnhandledEventException.class)
    public void givenAnErroneousEvent_whenHandlingIt_thenThrowsException() throws Exception {
        given(mockExtractor.extractFrom(mockEvent)).willThrow(ExtractException.class);

        issueHandler.handle(mockEvent);
    }

    @Test
    public void handledEventTypeShouldBeIssue() {
        assertEquals(EventType.ISSUE , issueHandler.handledEventType());
    }

}
