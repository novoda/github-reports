package com.novoda.handler;

import com.novoda.command.Options;
import com.novoda.core.stats.Stats;

public interface CommandHandler<T extends Stats, U extends Options> {

    T handle(U options);

}
