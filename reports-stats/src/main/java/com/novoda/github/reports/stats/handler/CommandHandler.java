package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.stats.command.Options;
import com.novoda.github.reports.data.model.Stats;

@FunctionalInterface
interface CommandHandler<T extends Stats, U extends Options> {

    T handle(U options);

}
