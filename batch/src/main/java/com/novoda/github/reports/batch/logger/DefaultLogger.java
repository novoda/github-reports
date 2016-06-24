package com.novoda.github.reports.batch.logger;

public class DefaultLogger implements Logger {

    private static final Level NO_LEVEL = null;

    private final LoggerHandler handler;
    private Level minimumLevel;

    public static DefaultLogger newInstance(LoggerHandler loggerHandler, Level minimumLevel) {
        return new DefaultLogger(loggerHandler, minimumLevel);
    }

    public static DefaultLogger newInstance(LoggerHandler loggerHandler) {
        return new DefaultLogger(loggerHandler, Level.INFO);
    }

    private DefaultLogger(LoggerHandler handler, Level minimumLevel) {
        this.handler = handler;
        setMinimumLevel(minimumLevel);
    }

    @Override
    public void logAll() {
        setMinimumLevel(Level.DEBUG);
    }

    @Override
    public void logNone() {
        setMinimumLevel(NO_LEVEL);
    }

    @Override
    public void setMinimumLevel(Level level) {
        this.minimumLevel = level;
    }

    @Override
    public void log(Level level, String message) {
        if (canLogLevel(level)) {
            String logString = getLogString(level, message);
            handler.log(logString);
        }
    }

    private boolean canLogLevel(Level level) {
        if (minimumLevel == NO_LEVEL) {
            return false;
        }
        return (minimumLevel.ordinal() <= level.ordinal());
    }

    private String getLogString(Level level, String message) {
        return String.format("[%s] %s", level.toString(), message);
    }
}
