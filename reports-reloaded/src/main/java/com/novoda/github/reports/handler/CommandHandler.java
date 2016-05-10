package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.Options;
import com.novoda.github.reports.core.stats.Stats;

public interface CommandHandler<T extends Stats, U extends Options> {

    T handle(U options);

}
