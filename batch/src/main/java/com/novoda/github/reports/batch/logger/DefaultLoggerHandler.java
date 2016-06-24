package com.novoda.github.reports.batch.logger;

public class DefaultLoggerHandler implements LoggerHandler {

    @Override
    public void log(String message) {
        System.out.println(message);
    }

}
