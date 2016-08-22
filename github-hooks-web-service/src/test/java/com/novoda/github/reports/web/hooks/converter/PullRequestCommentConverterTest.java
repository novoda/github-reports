package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequestComment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestCommentConverterTest {

    private static final long ANY_USER_ID = 88L;
    private static final long ANY_OWNER_ID = 86L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final int ANY_ISSUE_NUMBER = 23;
    private static final boolean ANY_IS_PULL_REQUEST = true;

    @InjectMocks
    private PullRequestCommentConverter converter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAPullRequestComment_whenConverting_thenConvertsSuccessfully() throws ConverterException {
        PullRequestComment pullRequestComment = givenAPullRequestComment();

        Event actual = converter.convertFrom(pullRequestComment);

        assertThat(actual).isEqualToComparingFieldByField(buildExpectedEvent());
    }

    private Event buildExpectedEvent() {
        return Event.create(
                ANY_COMMENT_ID,
                ANY_REPOSITORY_ID,
                ANY_USER_ID,
                ANY_OWNER_ID,
                EventType.PULL_REQUEST_COMMENT,
                ANY_DATE
        );
    }

    private PullRequestComment givenAPullRequestComment() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID);
        GithubIssue githubPullRequest = new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, ANY_IS_PULL_REQUEST);
        return new PullRequestComment(githubComment, githubRepository, githubPullRequest, GithubAction.CREATED);
    }
}
