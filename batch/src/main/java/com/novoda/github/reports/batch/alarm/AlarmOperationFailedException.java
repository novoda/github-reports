package com.novoda.github.reports.batch.alarm;

public class AlarmOperationFailedException extends Throwable {

    public AlarmOperationFailedException(String operation, Exception e) {
        super(String.format("Could not execute the alarm operation \"%s\".", operation), e);
    }

}
