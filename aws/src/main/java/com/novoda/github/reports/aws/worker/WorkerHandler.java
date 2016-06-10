package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

import java.util.List;

interface WorkerHandler<M extends QueueMessage> {

    List<M> handleQueueMessage(Configuration configuration, M queueMessage) throws RateLimitEncounteredException, Exception;

}
