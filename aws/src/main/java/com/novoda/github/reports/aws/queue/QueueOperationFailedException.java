package com.novoda.github.reports.aws.queue;

public class QueueOperationFailedException extends Exception {

    QueueOperationFailedException(String what) {
        super(String.format("The queue operation \"%s\" has failed.", what));
    }

}
