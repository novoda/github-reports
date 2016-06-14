package com.novoda.github.reports.aws.queue;

public interface QueueMessage {

    Boolean localTerminal();

    Long page();

}
