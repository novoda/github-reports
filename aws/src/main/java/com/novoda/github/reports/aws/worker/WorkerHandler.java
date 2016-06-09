package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

import java.util.List;

public interface WorkerHandler {

    List<QueueMessage> handleQueueMessage(Configuration configuration, QueueMessage queueMessage) throws RateLimitEncounteredException, Exception;

}
