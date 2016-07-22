package com.novoda.github.reports.stats.handler;

import com.novoda.github.reports.stats.command.Options;
import com.novoda.github.reports.data.model.Stats;

@FunctionalInterface
interface CommandHandler<S extends Stats, O extends Options> {

    S handle(O options);

}
