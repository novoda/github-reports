package com.novoda.github.reports.aws.queue;

public class EmptyQueueException extends Exception {

    EmptyQueueException(String message) {
        super(message);
    }

}
