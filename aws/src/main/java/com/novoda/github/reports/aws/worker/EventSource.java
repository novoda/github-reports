package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface EventSource<N extends NotifierConfiguration, C extends Configuration<N>> {

    C getConfiguration();

}
