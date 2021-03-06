package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.stats.command.UserOptions;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.UserDataLayer;
import com.novoda.github.reports.data.model.EventStats;
import com.novoda.github.reports.data.model.UserStats;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserCommandHandlerTest {

    private static final String ANY_USER = "uzer";
    private static final String ANY_REPO = "repoz";
    private static final Date ANY_FROM_DATE = new Date();
    private static final Date ANY_TO_DATE = new Date();
    private static final EventStats ANY_EVENT_STATS = new EventStats(
            BigInteger.valueOf(6),
            BigInteger.valueOf(5),
            BigInteger.valueOf(4),
            BigInteger.valueOf(3),
            BigInteger.valueOf(2)
    );
    private static final UserStats ANY_USER_STATS = new UserStats(ANY_USER, ANY_EVENT_STATS, BigInteger.valueOf(1), BigInteger.valueOf(0));

    @Mock
    UserOptions mockUserOptions;

    @Mock
    UserDataLayer mockDataLayer;

    private UserCommandHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockUserOptions.getUser()).thenReturn(ANY_USER);
        when(mockUserOptions.getRepository()).thenReturn(ANY_REPO);
        when(mockUserOptions.getFrom()).thenReturn(ANY_FROM_DATE);
        when(mockUserOptions.getTo()).thenReturn(ANY_TO_DATE);

        when(mockDataLayer.getStats(ANY_USER, ANY_REPO, ANY_FROM_DATE, ANY_TO_DATE)).thenReturn(ANY_USER_STATS);

        handler = new UserCommandHandler(mockDataLayer);
    }

    @Test
    public void whenHandlingAUserCommand_thenWeGetStatsForTheArgsPassedIn() throws DataLayerException {

        handler.handle(mockUserOptions);

        verify(mockDataLayer).getStats(ANY_USER, ANY_REPO, ANY_FROM_DATE, ANY_TO_DATE);
    }

    @Test
    public void whenHandlingAUserCommand_theReturnedStatsAreNotMutatedByTheHandler() {

        UserStats actual = handler.handle(mockUserOptions);

        assertEquals(ANY_USER_STATS, actual);
    }
}
