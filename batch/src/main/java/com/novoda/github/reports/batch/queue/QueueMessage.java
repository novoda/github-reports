package com.novoda.github.reports.batch.queue;

public interface QueueMessage {

    Boolean localTerminal();

    Long page();

}
