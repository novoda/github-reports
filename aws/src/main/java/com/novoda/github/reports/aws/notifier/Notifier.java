package com.novoda.github.reports.aws.notifier;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface Notifier<N extends NotifierConfiguration, C extends Configuration<N>> {

    void notifyCompletion(C configuration) throws NotifierOperationFailedException;

    void notifyError(C configuration, Throwable t) throws NotifierOperationFailedException;

}
