package com.novoda.github.reports.aws.worker;

public class WorkerOperationFailedException extends Exception {

    public WorkerOperationFailedException(String operation, Throwable cause) {
        super(String.format("Could not complete the worker operation \"%s\".", operation), cause);
    }

}
