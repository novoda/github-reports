package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface EventSource<C extends Configuration<? extends NotifierConfiguration>> {

    C getConfiguration();

}
