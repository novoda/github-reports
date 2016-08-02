package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.Issue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class IssueConverterTest {

    private static final long ANY_ISSUE_ID = 23L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_USER_ID = 88L;
    private static final Date ANY_DATE = new Date();

    @Parameterized.Parameters(name = "{index}: action={0}, expectedEvent={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { GithubAction.UNLABELED, EventType.ISSUE_LABEL_REMOVE },
                { GithubAction.OPENED, EventType.ISSUE_OPEN },
                { GithubAction.LABELED, EventType.ISSUE_LABEL_ADD },
                { GithubAction.CLOSED, EventType.ISSUE_CLOSE }
        });
    }

    @Parameter(0)
    public GithubAction action;

    @Parameter(1)
    public EventType expectedEventType;

    @InjectMocks
    private IssueConverter converter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAnIssue_whenConverting_thenConvertsSuccessfully() throws ConverterException {
        Issue issue = givenAnIssue();

        Event actual = converter.convertFrom(issue);

        assertThat(actual).isEqualToComparingFieldByField(buildExpectedEvent(expectedEventType));
    }

    private Issue givenAnIssue() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubIssue githubIssue = new GithubIssue(ANY_ISSUE_ID, ANY_DATE, githubUser, null);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        return new Issue(githubIssue, githubRepository, action);
    }

    private Event buildExpectedEvent(EventType eventType) {
        return Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, eventType, ANY_DATE);
    }

}
