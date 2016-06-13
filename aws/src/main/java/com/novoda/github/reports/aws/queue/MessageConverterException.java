package com.novoda.github.reports.aws.queue;

public class MessageConverterException extends Exception {

    MessageConverterException(Throwable cause) {
        super(cause);
    }

    MessageConverterException(String message) {
        super(message);
    }

}
