package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.MessageNotSupportedException;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

import java.util.List;

public interface WorkerHandler<M extends QueueMessage> {

    List<M> handleQueueMessage(Configuration configuration, M queueMessage)
            throws RateLimitEncounteredException, MessageNotSupportedException, TemporaryNetworkException;

}
