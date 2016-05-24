package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.ProjectOptions;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.ProjectDataLayer;
import com.novoda.github.reports.data.model.ProjectRepoStats;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProjectCommandHandlerTest {

    private static final String ANY_PROJECT = "projz";
    private static final Date ANY_FROM_DATE = new Date();
    private static final Date ANY_TO_DATE = new Date();
    private static final ProjectRepoStats ANY_REPO_STATS = new ProjectRepoStats(
            ANY_PROJECT,
            BigInteger.valueOf(6),
            BigInteger.valueOf(5),
            BigInteger.valueOf(4),
            BigInteger.valueOf(3),
            BigInteger.valueOf(2),
            BigInteger.valueOf(1)
    );

    @Mock
    ProjectOptions mockProjectOptions;

    @Mock
    ProjectDataLayer mockDataLayer;

    private ProjectCommandHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockProjectOptions.getProject()).thenReturn(ANY_PROJECT);
        when(mockProjectOptions.getFrom()).thenReturn(ANY_FROM_DATE);
        when(mockProjectOptions.getTo()).thenReturn(ANY_TO_DATE);

        when(mockDataLayer.getStats(ANY_PROJECT, ANY_FROM_DATE, ANY_TO_DATE)).thenReturn(ANY_REPO_STATS);

        handler = new ProjectCommandHandler(mockDataLayer);
    }

    @Test
    public void whenHandlingAProjectCommand_thenWeGetStatsForTheArgsPassedIn() throws DataLayerException {

        handler.handle(mockProjectOptions);

        verify(mockDataLayer).getStats(ANY_PROJECT, ANY_FROM_DATE, ANY_TO_DATE);
    }

    @Test
    public void whenHandlingAProjectCommand_theReturnedStatsAreNotMutatedByTheHandler() {

        ProjectRepoStats actual = handler.handle(mockProjectOptions);

        assertEquals(ANY_REPO_STATS, actual);
    }
}
