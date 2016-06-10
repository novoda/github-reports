package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.alarm.Alarm;
import com.novoda.github.reports.aws.alarm.AlarmService;
import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;
import com.novoda.github.reports.aws.notifier.Notifier;
import com.novoda.github.reports.aws.notifier.NotifierService;
import com.novoda.github.reports.aws.queue.EmptyQueueException;
import com.novoda.github.reports.aws.queue.MessageConverterException;
import com.novoda.github.reports.aws.queue.Queue;
import com.novoda.github.reports.aws.queue.QueueMessage;
import com.novoda.github.reports.aws.queue.QueueOperationFailedException;
import com.novoda.github.reports.aws.queue.QueueService;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;

import java.time.Instant;
import java.util.Date;
import java.util.List;

class BasicWorker<M extends QueueMessage, Q extends Queue<M>> implements Worker {

    private final WorkerService workerService;
    private final AlarmService alarmService;
    private final QueueService<Q> queueService;
    private final NotifierService notifierService;
    private final WorkerHandlerService<M> workerHandlerService;

    BasicWorker(WorkerService workerService,
                AlarmService alarmService,
                QueueService<Q> queueService,
                NotifierService notifierService,
                WorkerHandlerService<M> workerHandlerService) {
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

        Q queue = getQueue(eventSource);

        try {
            M queueMessage = queue.getItem();
            WorkerHandler<M> workerHandler = workerHandlerService.getWorkerHandler();

            List<M> newMessages = workerHandler.handleQueueMessage(eventSource.getConfiguration(), queueMessage);

            queue.removeItem(queueMessage);
            queue.addItems(newMessages);
        } catch (EmptyQueueException emptyQueue) {
            notifyCompletion(eventSource);
            queueService.removeQueue(queue);
            return;
        } catch (MessageConverterException | QueueOperationFailedException e) {
            notifyError(eventSource, e);
        } catch (RateLimitEncounteredException e) {
            rescheduleForLater(eventSource.getConfiguration(), differenceInMinutesFromNow(e.getResetDate()));
            return;
        } catch (Exception e) {
            queue.purgeQueue();
            queueService.removeQueue(queue);
            notifyError(eventSource, e);
            return;
        }

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

    private Q getQueue(EventSource eventSource) {
        Configuration configuration = eventSource.getConfiguration();
        String queueName = configuration.getQueueName();
        return queueService.getQueue(queueName);
    }

    private long differenceInMinutesFromNow(Date date) {
        Instant dateInstant = Instant.ofEpochMilli(date.getTime());
        long nowInstant = Instant.now().toEpochMilli();
        return Math.max(dateInstant.minusMillis(nowInstant).getEpochSecond(), 0L);
    }

    private void notifyError(EventSource eventSource, Exception exception) {
        exception.printStackTrace();

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
