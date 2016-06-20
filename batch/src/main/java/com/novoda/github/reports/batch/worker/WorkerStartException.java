package com.novoda.github.reports.batch.worker;

public class WorkerStartException extends Exception {

    public WorkerStartException(Exception e) {
        super(e);
    }

    public static WorkerStartException withStatusCode(Integer statusCode) {
        return new WorkerStartException(String.format("The worker was invoked but failed with status code %s.", statusCode));
    }

    private WorkerStartException(String message) {
        super(message);
    }
}
