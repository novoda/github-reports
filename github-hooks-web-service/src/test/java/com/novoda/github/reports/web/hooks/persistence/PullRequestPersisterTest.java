package com.novoda.github.reports.web.hooks.persistence;

import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.data.model.Repository;
import com.novoda.github.reports.data.model.User;
import com.novoda.github.reports.service.GithubUser;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.convert.EventConverter;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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

    @Mock
    private EventConverter<PullRequest, Event> mockConverter;

    @Mock
    private EventDataLayer mockEventDataLayer;

    @Mock
    private UserDataLayer mockUserDataLayer;

    @Mock
    private RepoDataLayer mockRepoDataLayer;

    @InjectMocks
    private PullRequestPersister pullRequestPersister;

    @Mock
    private PullRequest mockPullRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void givenAPullRequest_whenPersisting_thenTheUserIsPersisted() throws Exception {
        givenAPullRequest();

        pullRequestPersister.persist(mockPullRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserDataLayer).updateOrInsert(userCaptor.capture());
        assertThat(userCaptor.getValue()).isEqualToComparingFieldByField(User.create(ANY_USER_ID, ANY_USERNAME));
    }

    @Test
    public void givenAPullRequest_whenPersisting_thenTheRepositoryIsPersisted() throws Exception {
        givenAPullRequest();

        pullRequestPersister.persist(mockPullRequest);

        ArgumentCaptor<Repository> repositoryCaptor = ArgumentCaptor.forClass(Repository.class);
        verify(mockRepoDataLayer).updateOrInsert(repositoryCaptor.capture());
        assertThat(repositoryCaptor.getValue()).isEqualToComparingFieldByField(
                Repository.create(ANY_REPOSITORY_ID, ANY_REPOSITORY_NAME, ANY_IS_PRIVATE_REPOSITORY)
        );
    }

    @Test
    public void givenAPullRequest_whenPersisting_thenTheEventIsPersisted() throws Exception {
        givenAPullRequest();

        pullRequestPersister.persist(mockPullRequest);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(mockEventDataLayer).updateOrInsert(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isEqualToComparingFieldByField(
                Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, ANY_EVENT_TYPE, ANY_DATE)
        );
    }

    private void givenAPullRequest() {
        givenAUser();
        givenARepository();
        givenAnEvent();
    }

    private void givenAUser() {
        GithubUser mockUser = mock(GithubUser.class);
        given(mockUser.getUsername()).willReturn(ANY_USERNAME);

        GithubIssue mockIssue = mock(GithubIssue.class);
        given(mockIssue.getUserId()).willReturn(ANY_USER_ID);
        given(mockIssue.getUser()).willReturn(mockUser);

        given(mockPullRequest.getIssue()).willReturn(mockIssue);
    }

    private void givenARepository() {
        GithubRepository repository = mock(GithubRepository.class);
        given(repository.getId()).willReturn(ANY_REPOSITORY_ID);
        given(repository.getName()).willReturn(ANY_REPOSITORY_NAME);
        given(repository.isPrivateRepo()).willReturn(ANY_IS_PRIVATE_REPOSITORY);
        given(mockPullRequest.getRepository()).willReturn(repository);
    }

    private void givenAnEvent() {
        Event event = Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, ANY_EVENT_TYPE, ANY_DATE);
        try {
            given(mockConverter.convertFrom(mockPullRequest)).willReturn(event);
        } catch (ConverterException e) {
            // nothing to do
        }
    }

}
