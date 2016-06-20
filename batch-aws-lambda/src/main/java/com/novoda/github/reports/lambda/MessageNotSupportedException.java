package com.novoda.github.reports.lambda;

import com.novoda.github.reports.batch.queue.QueueMessage;

public class MessageNotSupportedException extends Exception {

    public MessageNotSupportedException(String message) {
        super(message);
    }

    public MessageNotSupportedException(QueueMessage queueMessage) {
        super("QueueMessage \"" + queueMessage.getClass().getSimpleName() + "\" not supported.");
    }
}
