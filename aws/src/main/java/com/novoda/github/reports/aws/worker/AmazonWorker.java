package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.alarm.AlarmService;
import com.novoda.github.reports.aws.notifier.NotifierService;
import com.novoda.github.reports.aws.queue.AmazonQueue;
import com.novoda.github.reports.aws.queue.AmazonQueueMessage;
import com.novoda.github.reports.aws.queue.QueueService;

public class AmazonWorker extends CommonWorker<AmazonQueueMessage, AmazonQueue> {

    private AmazonWorker(WorkerService workerService,
                         AlarmService alarmService,
                         QueueService<AmazonQueue> queueService,
                         NotifierService notifierService,
                         WorkerHandlerService<AmazonQueueMessage> workerHandlerService) {
        super(workerService, alarmService, queueService, notifierService, workerHandlerService);
    }
}
