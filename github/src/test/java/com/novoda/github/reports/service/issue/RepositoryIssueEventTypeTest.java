package com.novoda.github.reports.service.issue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class RepositoryIssueEventTypeTest {

    private static final GithubEvent.Type ANY_NON_SUPPORTED_EVENT_TYPE = GithubEvent.Type.UNLOCKED;
    private static final GithubEvent.Type ANY_SUPPORTED_EVENT_TYPE = GithubEvent.Type.COMMENTED;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void givenSupportedEventType_whenCreateTypeFromEvent_thenReturnCorrectMatch() {
        GithubEvent.Type type = ANY_SUPPORTED_EVENT_TYPE;

        RepositoryIssueEvent.Type actual = RepositoryIssueEvent.Type.fromEvent(type.toString());

        assertEquals(RepositoryIssueEvent.Type.COMMENTED, actual);
    }

    @Test
    public void givenNonSupportedEventType_whenCreateTypeFromEvent_thenThrowException() {
        GithubEvent.Type type = ANY_NON_SUPPORTED_EVENT_TYPE;

        expectedException.expect(IllegalArgumentException.class);

        RepositoryIssueEvent.Type.fromEvent(type.toString());
    }

}
