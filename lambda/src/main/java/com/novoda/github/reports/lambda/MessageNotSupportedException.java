package com.novoda.github.reports.lambda;

import com.novoda.github.reports.aws.queue.QueueMessage;

public class MessageNotSupportedException extends Exception {

    public MessageNotSupportedException(String message) {
        super(message);
    }

    MessageNotSupportedException(QueueMessage queueMessage) {
        super("QueueMessage \"" + queueMessage.getClass().getSimpleName() + "\" not supported.");
    }
}
