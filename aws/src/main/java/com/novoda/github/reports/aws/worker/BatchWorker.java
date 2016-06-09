package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.alarm.Alarm;
import com.novoda.github.reports.aws.alarm.AlarmService;
import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;
import com.novoda.github.reports.aws.notifier.Notifier;
import com.novoda.github.reports.aws.notifier.NotifierService;
import com.novoda.github.reports.aws.queue.Queue;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.aws.queue.QueueService;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BatchWorker implements Worker {

    private final WorkerService workerService;
    private final AlarmService alarmService;
    private final QueueService queueService;
    private final NotifierService notifierService;
    private final WorkerHandlerService workerHandlerService;

    public static BatchWorker newInstance(WorkerService workerService,
                                          AlarmService alarmService,
                                          QueueService queueService,
                                          NotifierService notifierService,
                                          WorkerHandlerService workerHandlerService) {
        return new BatchWorker(workerService, alarmService, queueService, notifierService, workerHandlerService);
    }

    private BatchWorker(WorkerService workerService,
                        AlarmService alarmService,
                        QueueService queueService,
                        NotifierService notifierService,
                        WorkerHandlerService workerHandlerService) {
        this.workerService = workerService;
        this.alarmService = alarmService;
        this.queueService = queueService;
        this.notifierService = notifierService;
        this.workerHandlerService = workerHandlerService;
    }

    @Override
    public void doWork(EventSource eventSource) {
        if (eventSource instanceof Alarm) {
            alarmService.removeAlarm((Alarm) eventSource);
        }

        Queue queue = getQueue(eventSource);
        if (queue.isEmpty()) {
            notifyCompletion(eventSource);
        }

        QueueMessage queueMessage = queue.getItem();
        List<QueueMessage> newMessages = Collections.emptyList();

        try {
            WorkerHandler workerHandler = workerHandlerService.getWorkerHandler();
            newMessages = workerHandler.handleQueueMessage(eventSource.getConfiguration(), queueMessage);
        } catch (RateLimitEncounteredException e) {
            rescheduleForLater(eventSource.getConfiguration(), differenceInMinutesFromNow(e.getResetDate()));
            return;
        } catch (Exception e) {
            queue.purgeQueue();
            notifyError(eventSource, e);
        }

        queue.removeItem(queueMessage);
        queue.addItems(newMessages);

        rescheduleImmediately(eventSource.getConfiguration());
    }

    @Override
    public void rescheduleImmediately(Configuration configuration) {
        workerService.startWorker(configuration);
    }

    @Override
    public void rescheduleForLater(Configuration configuration, long minutes) {
        Alarm alarm = alarmService.createAlarm(configuration, minutes);
        alarmService.postAlarm(alarm);
    }

    private void notifyCompletion(EventSource eventSource) {
        Notifier notifier = getNotifier();
        NotifierConfiguration notifierConfiguration = getNotifierConfiguration(eventSource);
        notifier.notifyCompletion(notifierConfiguration);
    }

    private Queue getQueue(EventSource eventSource) {
        Configuration configuration = eventSource.getConfiguration();
        String queueName = configuration.getQueueName();
        return queueService.getQueue(queueName);
    }

    private long differenceInMinutesFromNow(Date date) {
        Instant dateInstant = Instant.ofEpochMilli(date.getTime());
        long nowInstant = Instant.now().toEpochMilli();
        return dateInstant.minusMillis(nowInstant).getEpochSecond();
    }

    private void notifyError(EventSource eventSource, Exception exception) {
        Notifier notifier = getNotifier();
        NotifierConfiguration notifierConfiguration = getNotifierConfiguration(eventSource);
        notifier.notifyError(notifierConfiguration, exception);
    }

    private Notifier getNotifier() {
        return notifierService.getNotifier();
    }

    private NotifierConfiguration getNotifierConfiguration(EventSource eventSource) {
        return eventSource.getConfiguration().getNotifierConfiguration();
    }
}
