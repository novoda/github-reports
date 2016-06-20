package com.novoda.github.reports.batch.notifier;

public class NotifierOperationFailedException extends Exception {

    public NotifierOperationFailedException(Exception e) {
        super(e);
    }

}
