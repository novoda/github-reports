package com.novoda.github.reports.web.hooks.converter;

import com.novoda.github.reports.service.persistence.converter.ConverterException;
import com.novoda.github.reports.service.pullrequest.GithubPullRequest;
import com.novoda.github.reports.web.hooks.model.GithubAction;
import com.novoda.github.reports.web.hooks.model.PullRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestConverterUnsupportedActionTest {

    private static final GithubAction ANY_UNSUPPORTED_ACTION = GithubAction.SYNCHRONIZE;

    @Mock
    private GithubPullRequest mockGithubPullRequest;

    @Mock
    private PullRequest mockPullRequest;

    @InjectMocks
    private PullRequestConverter converter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test(expected = ConverterException.class)
    public void givenAPullRequestWithAnUnsupportedAction_whenConverting_thenThrowsException() throws ConverterException {
        given(mockPullRequest.getAction()).willReturn(ANY_UNSUPPORTED_ACTION);

        converter.convertFrom(mockPullRequest);
    }
}
