package com.novoda.github.reports.aws.notifier;

import com.novoda.github.reports.aws.configuration.NotifierConfiguration;

public interface Notifier {

    void notifyCompletion(NotifierConfiguration configuration);

    void notifyError(NotifierConfiguration configuration, Throwable t);

}
