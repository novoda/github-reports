package com.novoda.github.reports.service.issue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class RepositoryIssueEventTypeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void givenExistingEventType_whenCreateTypeFromEvent_thenReturnCorrectMatch() {
        GithubEvent.Type type = GithubEvent.Type.COMMENTED;

        RepositoryIssueEvent.Type actual = RepositoryIssueEvent.Type.fromEvent(type.toString());

        assertEquals(RepositoryIssueEvent.Type.COMMENTED, actual);
    }

    @Test
    public void givenNonExistingEventType_whenCreateTypeFromEvent_thenThrowException() {
        GithubEvent.Type type = GithubEvent.Type.UNLOCKED;

        expectedException.expect(IllegalArgumentException.class);

        RepositoryIssueEvent.Type.fromEvent(type.toString());
    }

}
