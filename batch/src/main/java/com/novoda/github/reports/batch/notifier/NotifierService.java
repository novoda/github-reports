package com.novoda.github.reports.batch.notifier;

import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;

public interface NotifierService<N extends NotifierConfiguration, C extends Configuration<N>> {

    Notifier<N, C> getNotifier();

}
