package com.novoda.github.reports.batch.queue;

public class QueueOperationFailedException extends Exception {

    public QueueOperationFailedException(String what) {
        super(String.format("The queue operation \"%s\" has failed.", what));
    }

}
