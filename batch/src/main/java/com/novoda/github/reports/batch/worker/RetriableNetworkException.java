package com.novoda.github.reports.batch.worker;

public class RetriableNetworkException extends Exception {

    public RetriableNetworkException(Throwable cause) {
        super(cause);
    }

}
