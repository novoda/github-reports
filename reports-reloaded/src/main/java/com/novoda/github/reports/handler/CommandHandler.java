package com.novoda.github.reports.handler;

import com.novoda.github.reports.command.Options;
import com.novoda.github.reports.data.model.Stats;

interface CommandHandler<T extends Stats, U extends Options> {

    T handle(U options);

}
