package com.novoda.github.reports.aws.notifier;

import com.novoda.github.reports.aws.worker.NotifierConfiguration;

interface Notifier {

    void notifyCompletion(NotifierConfiguration configuration);

    void notifyError(NotifierConfiguration configuration, Exception exception);

}
