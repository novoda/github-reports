package com.novoda.github.reports.web.hooks.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubComment;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequestComment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestCommentPersisterTest {

    private static final String ANY_USERNAME = "uzer";
    private static final long ANY_USER_ID = 88L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_COMMENT_ID = 23L;
    private static final Date ANY_DATE = new Date();
    private static final String ANY_REPOSITORY_NAME = "topbantz";
    private static final boolean ANY_IS_PRIVATE_REPOSITORY = true;
    private static final int ANY_ISSUE_NUMBER = 23;
    private static final long ANY_OWNER_ID = 86L;
    private static final boolean ANY_IS_PULL_REQUEST = true;

    @Mock
    private EventConverter<PullRequestComment> mockConverter;

    @Mock
    private EventDataLayer mockEventDataLayer;

    @Mock
    private UserDataLayer mockUserDataLayer;

    @Mock
    private RepoDataLayer mockRepoDataLayer;

    @InjectMocks
    private PullRequestCommentPersister persister;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAPullRequestComment_whenPersisting_thenTheUserIsPersisted() throws Exception {
        PullRequestComment pullRequestComment = givenAPullRequestComment();
        givenAnEvent(pullRequestComment);

        persister.persist(pullRequestComment);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDataLayer).updateOrInsert(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualToComparingFieldByField(User.create(ANY_USER_ID, ANY_USERNAME));
    }

    @Test
    public void givenAPullRequestComment_whenPersisting_thenTheRepositoryIsPersisted() throws Exception {
        PullRequestComment pullRequestComment = givenAPullRequestComment();
        givenAnEvent(pullRequestComment);

        persister.persist(pullRequestComment);

        ArgumentCaptor<Repository> repositoryCaptor = ArgumentCaptor.forClass(Repository.class);
        verify(mockRepoDataLayer).updateOrInsert(repositoryCaptor.capture());
        assertThat(repositoryCaptor.getValue()).isEqualToComparingFieldByField(
                Repository.create(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY)
        );
    }

    @Test
    public void givenAPullRequestComment_whenPersisting_thenTheEventIsPersisted() throws Exception {
        PullRequestComment pullRequestComment = givenAPullRequestComment();
        givenAnEvent(pullRequestComment);

        persister.persist(pullRequestComment);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(mockEventDataLayer).updateOrInsert(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualToComparingFieldByField(
                Event.create(ANY_COMMENT_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, EventType.PULL_REQUEST_COMMENT, ANY_DATE)
        );
    }

    private PullRequestComment givenAPullRequestComment() {
        GithubUser githubUser = new GithubUser(ANY_USER_ID, ANY_USERNAME);
        GithubComment githubComment = new GithubComment(ANY_COMMENT_ID, githubUser, ANY_DATE);
        GithubRepository githubRepository = new GithubRepository(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY);
        GithubIssue githubIssue = new GithubIssue(ANY_ISSUE_NUMBER, ANY_OWNER_ID, ANY_IS_PULL_REQUEST);
        return new PullRequestComment(githubComment, githubRepository, githubIssue, GithubAction.CREATED);
    }

    private void givenAnEvent(PullRequestComment pullRequestComment) {
        Event event = Event.create(ANY_COMMENT_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, EventType.PULL_REQUEST_COMMENT, ANY_DATE);
        try {
            given(mockConverter.convertFrom(pullRequestComment)).willReturn(event);
        } catch (ConverterException e) {
            // nothing to do
        }
    }

}
