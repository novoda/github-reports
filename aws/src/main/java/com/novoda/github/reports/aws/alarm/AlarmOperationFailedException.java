package com.novoda.github.reports.aws.alarm;

public class AlarmOperationFailedException extends Throwable {

    AlarmOperationFailedException(String operation, Exception e) {
        super(String.format("Could not execute the alarm operation \"%s\".", operation), e);
    }

}
