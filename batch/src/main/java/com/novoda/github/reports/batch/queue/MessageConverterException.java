package com.novoda.github.reports.batch.queue;

public class MessageConverterException extends Exception {

    public MessageConverterException(Throwable cause) {
        super(cause);
    }

    public MessageConverterException(String message) {
        super(message);
    }

}
