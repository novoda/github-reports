package com.novoda.github.reports.batch.handler;

import com.novoda.github.reports.batch.command.BatchOptions;

@FunctionalInterface
public interface CommandHandler<O extends BatchOptions> {

    void handle(O options) throws Exception;

}
