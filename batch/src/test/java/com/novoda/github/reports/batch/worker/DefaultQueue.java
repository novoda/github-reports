package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.queue.Queue;
import com.novoda.github.reports.batch.queue.QueueMessage;

interface DefaultQueue extends Queue<QueueMessage> {
}
