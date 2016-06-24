package com.novoda.github.reports.batch.queue;

public interface QueueMessage {

    Boolean localTerminal();

    Long page();

    String toShortString();

    default String getPageAndTerminalString() {
        return String.format("(page %d%s)", page(), getTerminalString());
    }

    default String getTerminalString() {
        if (localTerminal()) {
            return "";
        }
        return " is terminal";
    }
}
