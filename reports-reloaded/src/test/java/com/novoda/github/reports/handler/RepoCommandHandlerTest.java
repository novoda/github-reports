package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.RepoOptions;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepoCommandHandlerTest {

    private static final String ANY_REPO = "repo";
    private static final Date ANY_FROM_DATE = new Date();
    private static final Date ANY_TO_DATE = new Date();
    private static final ProjectRepoStats ANY_REPO_STATS = new ProjectRepoStats(ANY_REPO, 6, 5, 4, 3, 2, 1);

    @Mock
    RepoOptions mockRepoOptions;

    @Mock
    RepoDataLayer mockDataLayer;

    private RepoCommandHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockRepoOptions.getRepo()).thenReturn(ANY_REPO);
        when(mockRepoOptions.getFrom()).thenReturn(ANY_FROM_DATE);
        when(mockRepoOptions.getTo()).thenReturn(ANY_TO_DATE);

        when(mockDataLayer.getStats(ANY_REPO, ANY_FROM_DATE, ANY_TO_DATE)).thenReturn(ANY_REPO_STATS);

        handler = new RepoCommandHandler(mockDataLayer);
    }

    @Test
    public void givenADataLayer_whenHandlingARepoCommand_thenWeGetStatsForTheArgsPassedIn() {

        handler.handle(mockRepoOptions);

        verify(mockDataLayer).getStats(ANY_REPO, ANY_FROM_DATE, ANY_TO_DATE);
    }

    @Test
    public void givenADataLayer_whenHandlingARepoCommand_theReturnedStatsAreNotMutatedByTheHandler() {

        ProjectRepoStats actual = handler.handle(mockRepoOptions);

        assertEquals(ANY_REPO_STATS, actual);
    }

}
