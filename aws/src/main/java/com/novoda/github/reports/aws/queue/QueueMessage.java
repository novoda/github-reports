package com.novoda.github.reports.aws.queue;

public interface QueueMessage {

    boolean localTerminal();

    Long page();

}
