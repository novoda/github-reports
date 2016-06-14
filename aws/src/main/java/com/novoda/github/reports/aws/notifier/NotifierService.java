package com.novoda.github.reports.aws.notifier;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface NotifierService<N extends NotifierConfiguration, C extends Configuration<N>> {

    Notifier<N, C> getNotifier();

}
