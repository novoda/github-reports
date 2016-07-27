package com.novoda.github.reports.web.hooks.convert;

import com.novoda.github.reports.data.model.Event;
import com.novoda.github.reports.data.model.EventType;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.novoda.github.reports.web.hooks.model.GithubAction.OPENED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestToDbEventConverterTest {

    private static final long ANY_ISSUE_ID = 23L;
    private static final long ANY_REPOSITORY_ID = 42L;
    private static final long ANY_USER_ID = 88L;
    private static final Date ANY_DATE = new Date();

    @Mock
    private GithubIssue mockIssue;

    @Mock
    private GithubRepository mockRepository;

    @Mock
    private PullRequest pullRequest;

    @InjectMocks
    private PullRequestToDbEventConverter converter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenAPullRequest_whenConverting_thenConvertsSuccessfully() throws ConverterException {
        givenAPullRequestThatWas(OPENED);

        Event actual = converter.convertFrom(pullRequest);

        assertThat(actual).isEqualToComparingFieldByField(
                Event.create(ANY_ISSUE_ID, ANY_REPOSITORY_ID, ANY_USER_ID, ANY_USER_ID, EventType.PULL_REQUEST_OPEN, ANY_DATE)
        );
    }

    private void givenAPullRequestThatWas(GithubAction action) {
        given(mockIssue.getId()).willReturn(ANY_ISSUE_ID);
        given(mockRepository.getId()).willReturn(ANY_REPOSITORY_ID);
        given(mockIssue.getUserId()).willReturn(ANY_USER_ID);
        given(mockIssue.getUpdatedAt()).willReturn(ANY_DATE);
        given(pullRequest.getAction()).willReturn(action);
        given(pullRequest.getIssue()).willReturn(mockIssue);
        given(pullRequest.getRepository()).willReturn(mockRepository);
    }

    @Test(expected = ConverterException.class)
    public void givenAPullRequest_whenConverting_thenThrowsException() throws ConverterException {



    }

    // @RUI parameterise these tests? take out the exception and action conversion ones
    @Test
    public void convertAction() throws Exception {

    }

}
