package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.queue.Queue;
import com.novoda.github.reports.aws.queue.QueueMessage;

interface DefaultQueue extends Queue<QueueMessage> {
}
