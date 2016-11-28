package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.convert.FailedToLoadMappingsException;
import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.EventStats;
import com.novoda.github.reports.data.model.ProjectRepoStats;
import com.novoda.github.reports.stats.command.ProjectOptions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProjectCommandHandlerTest {

    private static final String ANY_PROJECT = "some-project";
    private static final Date ANY_FROM_DATE = new Date();
    private static final Date ANY_TO_DATE = new Date();
    private static final EventStats ANY_EVENT_STATS = new EventStats(
            BigInteger.valueOf(6),
            BigInteger.valueOf(5),
            BigInteger.valueOf(4),
            BigInteger.valueOf(3),
            BigInteger.valueOf(2)
    );

    private static final ProjectOptions mockProjectOptions = new ProjectOptions(
            Collections.singletonList(ANY_PROJECT),
            ANY_FROM_DATE,
            ANY_TO_DATE
    );

    private static final String ANY_REPO = "a-repository";
    private static final String ANY_OTHER_REPO = "another-repo";
    private static final List<String> ANY_PROJECT_REPO_LIST = Arrays.asList(ANY_REPO, ANY_OTHER_REPO);
    private static final ProjectRepoStats ANY_PROJECT_STATS = new ProjectRepoStats(
            ANY_REPO + ", " + ANY_OTHER_REPO,
            ANY_EVENT_STATS,
            BigInteger.valueOf(1)
    );

    @Mock
    RepoDataLayer mockDataLayer;

    @Mock
    FloatGithubProjectConverter mockConverter;

    @InjectMocks
    private ProjectCommandHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void whenHandlingAProjectCommand_thenWeGetStatsForTheArgsPassedIn() throws DataLayerException, FailedToLoadMappingsException {
        given(mockConverter.getRepositories(ANY_PROJECT)).willReturn(ANY_PROJECT_REPO_LIST);
        given(mockDataLayer.getStats(ANY_PROJECT_REPO_LIST, ANY_FROM_DATE, ANY_TO_DATE)).willReturn(ANY_PROJECT_STATS);

        handler.handle(mockProjectOptions);

        verify(mockDataLayer).getStats(ANY_PROJECT_REPO_LIST, ANY_FROM_DATE, ANY_TO_DATE);
    }

    @Test
    public void whenHandlingAProjectCommand_theReturnedStatsAreMutatedByTheHandler() throws DataLayerException, FailedToLoadMappingsException {
        given(mockConverter.getRepositories(ANY_PROJECT)).willReturn(ANY_PROJECT_REPO_LIST);
        given(mockDataLayer.getStats(ANY_PROJECT_REPO_LIST, ANY_FROM_DATE, ANY_TO_DATE)).willReturn(ANY_PROJECT_STATS);

        ProjectRepoStats actual = handler.handle(mockProjectOptions);

        assertEquals(mockProjectOptions.getProject(), actual.getProjectRepoName());
    }
}
