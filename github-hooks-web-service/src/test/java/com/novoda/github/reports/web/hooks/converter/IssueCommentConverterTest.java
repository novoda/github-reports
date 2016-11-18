package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.IssueComment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class IssueCommentConverterTest {

    private static final long ANY_USER_ID = 88L;
    private static final long ANY_OWNER_ID = 86L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final int ANY_ISSUE_NUMBER = 23;
    private static final boolean IS_NOT_PULL_REQUEST = false;

    @InjectMocks
    private IssueCommentConverter converter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAnIssueComment_whenConverting_thenConvertsSuccessfully() throws ConverterException {
        IssueComment issueComment = givenAnIssueComment();

        Event actual = converter.convertFrom(issueComment);

        assertThat(actual).isEqualToComparingFieldByField(buildExpectedEvent());
    }

    private Event buildExpectedEvent() {
        return Event.create(
                ANY_COMMENT_ID,
                ANY_REPOSITORY_ID,
                ANY_USER_ID,
                ANY_OWNER_ID,
                EventType.ISSUE_COMMENT,
                ANY_DATE
        );
    }

    private IssueComment givenAnIssueComment() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        GithubIssue githubIssue = new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, IS_NOT_PULL_REQUEST);
        return new IssueComment(githubComment, githubRepository, githubIssue, GithubAction.CREATED);
    }
}
