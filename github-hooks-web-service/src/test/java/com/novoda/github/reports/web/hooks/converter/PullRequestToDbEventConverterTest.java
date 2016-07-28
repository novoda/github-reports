package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.mockito.InjectMocks;

import static com.novoda.github.reports.web.hooks.model.GithubAction.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class PullRequestToDbEventConverterTest {

    private static final int ANY_ISSUE_NUMBER = 23;

    @Parameterized.Parameters(name = "{index}: action={0}, isPrMerged={1} expectedEvent={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {OPENED, false, EventType.PULL_REQUEST_OPEN},
                {CLOSED, false, EventType.PULL_REQUEST_CLOSE},
                {CLOSED, true, EventType.PULL_REQUEST_MERGE},
                {LABELED, false, EventType.PULL_REQUEST_LABEL_ADD},
                {UNLABELED, false, EventType.PULL_REQUEST_LABEL_REMOVE}
        });
    }

    private static final long ANY_ISSUE_ID = 23L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_USER_ID = 88L;
    private static final Date ANY_DATE = new Date();

    @Parameter(0)
    public GithubAction action;

    @Parameter(1)
    public boolean isPullRequestMerged;

    @Parameter(2)
    public EventType expectedEventType;

    @InjectMocks
    private PullRequestToDbEventConverter converter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAPullRequest_whenConverting_thenConvertsSuccessfully() throws ConverterException {
        PullRequest pullRequest = givenAPullRequest();

        Event actual = converter.convertFrom(pullRequest);

        assertThat(actual).isEqualToComparingFieldByField(buildExpectedEvent(expectedEventType));
    }

    private PullRequest givenAPullRequest() {
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubWebhookPullRequest githubIssue = new GithubWebhookPullRequest(ANY_ISSUE_NUMBER, ANY_DATE, githubUser, isPullRequestMerged);

        return new PullRequest(githubIssue, githubRepository, action);
    }

    private Event buildExpectedEvent(EventType eventType) {
        return Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, eventType, ANY_DATE);
    }
}
