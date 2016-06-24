package com.novoda.github.reports.batch.logger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DefaultLoggerTest {

    private static final String ANY_MESSAGE = "this is a log message";

    @Mock
    LoggerHandler loggerHandler;

    private DefaultLogger logger;

    @Before
    public void setUp() {
        initMocks(this);
        logger = DefaultLogger.newInstance(loggerHandler);
    }

    @Test
    public void givenLogger_whenLogInfo_thenLogMinimumLevelMessage() {

        logger.info(ANY_MESSAGE);

        verify(loggerHandler).log("[ INFO ] " + ANY_MESSAGE);
    }

    @Test
    public void givenSilentLogger_whenLogAnyLevel_thenDoNotLog() {
        logger.logNone();

        logger.debug(ANY_MESSAGE);
        logger.error(ANY_MESSAGE);

        verifyDoNotLog();
    }

    @Test
    public void givenSilentLogger_whenLogAllAndLogDebug_thenLogMinimumLevelMessage() {
        logger.logNone();

        logger.logAll();
        logger.debug(ANY_MESSAGE);

        verify(loggerHandler).log("[ DEBUG ] " + ANY_MESSAGE);
    }

    @Test
    public void givenLoggerAtWarning_whenLogInfo_thenDoNotLog() {
        logger.setMinimumLevel(Logger.Level.WARNING);

        logger.info(ANY_MESSAGE);

        verifyDoNotLog();
    }

    @Test
    public void givenLoggerAtWarning_whenLogError_thenLogError() {
        logger.setMinimumLevel(Logger.Level.WARNING);

        logger.error(ANY_MESSAGE);

        verify(loggerHandler).log("[ ERROR ] \u274C  " + ANY_MESSAGE);
    }

    @Test
    public void givenLoggerAtError_whenLogError_thenLogError() {
        logger.setMinimumLevel(Logger.Level.ERROR);

        logger.error(ANY_MESSAGE);

        verify(loggerHandler).log("[ ERROR ] \u274C  " + ANY_MESSAGE);
    }

    @Test
    public void givenLoggerAtWarning_whenLogWarning_thenLogWarning() {
        logger.setMinimumLevel(Logger.Level.WARNING);

        logger.warn(ANY_MESSAGE);

        verify(loggerHandler).log("[ WARNING ] \u26A0  " + ANY_MESSAGE);
    }

    @Test
    public void givenLoggerAtInfo_whenLogInfo_thenLogInfo() {
        logger.setMinimumLevel(Logger.Level.INFO);

        logger.info(ANY_MESSAGE);

        verify(loggerHandler).log("[ INFO ] " + ANY_MESSAGE);
    }

    @Test
    public void givenLoggerAtDebug_whenLogDebug_thenLogDebug() {
        logger.setMinimumLevel(Logger.Level.DEBUG);

        logger.debug(ANY_MESSAGE);

        verify(loggerHandler).log("[ DEBUG ] " + ANY_MESSAGE);
    }

    private void verifyDoNotLog() {
        verify(loggerHandler, never()).log(anyString());
    }

}
