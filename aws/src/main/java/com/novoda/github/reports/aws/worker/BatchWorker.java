package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.alarm.Alarm;
import com.novoda.github.reports.aws.alarm.AlarmServiceClient;
import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;
import com.novoda.github.reports.aws.notifier.Notifier;
import com.novoda.github.reports.aws.notifier.NotifierServiceClient;
import com.novoda.github.reports.aws.queue.Queue;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.aws.queue.QueueServiceClient;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BatchWorker implements Worker {

    private final WorkerServiceClient workerServiceClient;
    private final AlarmServiceClient alarmServiceClient;
    private final QueueServiceClient queueServiceClient;
    private final NotifierServiceClient notifierServiceClient;
    private final WorkerHandlerServiceClient workerHandlerServiceClient;

    public static BatchWorker newInstance(WorkerServiceClient workerServiceClient,
                                          AlarmServiceClient alarmServiceClient,
                                          QueueServiceClient queueServiceClient,
                                          NotifierServiceClient notifierServiceClient,
                                          WorkerHandlerServiceClient workerHandlerServiceClient) {
        return new BatchWorker(workerServiceClient, alarmServiceClient, queueServiceClient, notifierServiceClient, workerHandlerServiceClient);
    }

    private BatchWorker(WorkerServiceClient workerServiceClient,
                        AlarmServiceClient alarmServiceClient,
                        QueueServiceClient queueServiceClient,
                        NotifierServiceClient notifierServiceClient,
                        WorkerHandlerServiceClient workerHandlerServiceClient) {
        this.workerServiceClient = workerServiceClient;
        this.alarmServiceClient = alarmServiceClient;
        this.queueServiceClient = queueServiceClient;
        this.notifierServiceClient = notifierServiceClient;
        this.workerHandlerServiceClient = workerHandlerServiceClient;
    }

    @Override
    public void doWork(EventSource eventSource) {
        if (eventSource instanceof Alarm) {
            alarmServiceClient.removeAlarm((Alarm) eventSource);
        }

        Queue queue = getQueue(eventSource);
        if (queue.isEmpty()) {
            notifyCompletion(eventSource);
        }

        QueueMessage queueMessage = queue.getItem();
        List<QueueMessage> newMessages = Collections.emptyList();

        try {
            WorkerHandler workerHandler = workerHandlerServiceClient.getWorkerHandler();
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
        workerServiceClient.startWorker(configuration);
    }

    @Override
    public void rescheduleForLater(Configuration configuration, long minutes) {
        Alarm alarm = alarmServiceClient.createAlarm(configuration, minutes);
        alarmServiceClient.postAlarm(alarm);
    }

    private void notifyCompletion(EventSource eventSource) {
        Notifier notifier = getNotifier();
        NotifierConfiguration notifierConfiguration = getNotifierConfiguration(eventSource);
        notifier.notifyCompletion(notifierConfiguration);
    }

    private Queue getQueue(EventSource eventSource) {
        Configuration configuration = eventSource.getConfiguration();
        String queueName = configuration.getQueueName();
        return queueServiceClient.getQueue(queueName);
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
        return notifierServiceClient.getNotifier();
    }

    private NotifierConfiguration getNotifierConfiguration(EventSource eventSource) {
        return eventSource.getConfiguration().getNotifierConfiguration();
    }
}
