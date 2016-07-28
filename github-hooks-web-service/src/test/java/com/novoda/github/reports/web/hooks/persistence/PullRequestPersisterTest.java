package com.novoda.github.reports.web.hooks.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.converter.EventConverter;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.GithubWebhookPullRequest;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestPersisterTest {

    private static final long ANY_ISSUE_ID = 23L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_USER_ID = 88L;
    private static final Date ANY_DATE = new Date();
    private static final EventType ANY_EVENT_TYPE = EventType.BRANCH_DELETE;
    private static final String ANY_USERNAME = "dudelio";
    private static final String ANY_REPOSITORY_NAME = "topbantz";
    private static final boolean ANY_IS_PRIVATE_REPOSITORY = false;
    private static final GithubAction ANY_ACTION = GithubAction.ADDED;
    private static final boolean ANY_PULL_REQUEST_WAS_MERGED = false;

    @Mock
    private EventConverter<PullRequest> mockConverter;

    @Mock
    private EventDataLayer mockEventDataLayer;

    @Mock
    private UserDataLayer mockUserDataLayer;

    @Mock
    private RepoDataLayer mockRepoDataLayer;

    @InjectMocks
    private PullRequestPersister pullRequestPersister;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAPullRequest_whenPersisting_thenTheUserIsPersisted() throws Exception {
        PullRequest pullRequest = givenAPullRequest();
        givenAnEvent(pullRequest);

        pullRequestPersister.persist(pullRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDataLayer).updateOrInsert(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualToComparingFieldByField(User.create(ANY_USER_ID, ANY_USERNAME));
    }

    @Test
    public void givenAPullRequest_whenPersisting_thenTheRepositoryIsPersisted() throws Exception {
        PullRequest pullRequest = givenAPullRequest();
        givenAnEvent(pullRequest);

        pullRequestPersister.persist(pullRequest);

        ArgumentCaptor<Repository> repositoryCaptor = ArgumentCaptor.forClass(Repository.class);
        verify(mockRepoDataLayer).updateOrInsert(repositoryCaptor.capture());
        assertThat(repositoryCaptor.getValue()).isEqualToComparingFieldByField(
                Repository.create(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY)
        );
    }

    @Test
    public void givenAPullRequest_whenPersisting_thenTheEventIsPersisted() throws Exception {
        PullRequest pullRequest = givenAPullRequest();
        givenAnEvent(pullRequest);

        pullRequestPersister.persist(pullRequest);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(mockEventDataLayer).updateOrInsert(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualToComparingFieldByField(
                Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, ANY_EVENT_TYPE, ANY_DATE)
        );
    }

    private PullRequest givenAPullRequest() {
        GithubWebhookPullRequest githubIssue = givenAnIssue();
        return new PullRequest(githubIssue, givenARepository(), ANY_ACTION);
    }

    private GithubUser givenAUser() {
        return new GithubUser(ANY_USER_ID, ANY_USERNAME);
    }

    private GithubWebhookPullRequest givenAnIssue() {
        return new GithubWebhookPullRequest(ANY_ISSUE_ID, ANY_DATE, givenAUser(), ANY_PULL_REQUEST_WAS_MERGED);
    }

    private GithubRepository givenARepository() {
        return new GithubRepository(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY);
    }

    private void givenAnEvent(PullRequest pullRequest) {
        Event event = Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, ANY_EVENT_TYPE, ANY_DATE);
        try {
            given(mockConverter.convertFrom(pullRequest)).willReturn(event);
        } catch (ConverterException e) {
            // nothing to do
        }
    }

}
