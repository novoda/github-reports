package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.alarm.Alarm;
import com.novoda.github.reports.aws.alarm.AlarmOperationFailedException;
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
import com.novoda.github.reports.util.SystemClock;

import java.util.List;

class BasicWorker<
        A extends Alarm,
        M extends QueueMessage,
        Q extends Queue<M>,
        C extends Configuration<NotifierConfiguration>>
        implements Worker<C> {

    private final WorkerService workerService;
    private final AlarmService<A, C> alarmService;
    private final QueueService<Q> queueService;
    private final NotifierService notifierService;
    private final WorkerHandlerService<M> workerHandlerService;
    private final SystemClock systemClock;

    public static <
            A extends Alarm,
            M extends QueueMessage,
            Q extends Queue<M>,
            C extends Configuration<NotifierConfiguration>> BasicWorker<A, M, Q, C> newInstance(WorkerService workerService,
                                                                                                AlarmService<A, C> alarmService,
                                                                                                QueueService<Q> queueService,
                                                                                                NotifierService notifierService,
                                                                                                WorkerHandlerService<M> workerHandlerService) {
        SystemClock systemClock = SystemClock.newInstance();
        return new BasicWorker<>(workerService, alarmService, queueService, notifierService, workerHandlerService, systemClock);
    }

    private BasicWorker(WorkerService workerService,
                        AlarmService<A, C> alarmService,
                        QueueService<Q> queueService,
                        NotifierService notifierService,
                        WorkerHandlerService<M> workerHandlerService,
                        SystemClock systemClock) {
        this.workerService = workerService;
        this.alarmService = alarmService;
        this.queueService = queueService;
        this.notifierService = notifierService;
        this.workerHandlerService = workerHandlerService;
        this.systemClock = systemClock;
    }

    @Override
    public void doWork(EventSource<C> eventSource) throws WorkerOperationFailedException {
        if (eventSource instanceof Alarm) {
            alarmService.removeAlarm((A) eventSource);
        }

        Q queue = getQueue(eventSource);

        try {
            M queueMessage = queue.getItem();
            List<M> newMessages = handleQueueMessage(eventSource, queueMessage);
            updateQueue(queue, queueMessage, newMessages);
            rescheduleImmediately(eventSource.getConfiguration());
        } catch (EmptyQueueException emptyQueue) {
            handleEmptyQueueException(eventSource, queue);
        } catch (MessageConverterException e) {
            handleMessageConverterException(eventSource, e);
        } catch (RateLimitEncounteredException e) {
            handleRateLimitEncounteredException(eventSource, e);
        } catch (QueueOperationFailedException e) {
            handleQueueOperationFailedException(eventSource, e);
        } catch (Throwable t) {
            handleAnyOtherException(eventSource, queue, t);
        }
    }

    private Q getQueue(EventSource eventSource) {
        Configuration configuration = eventSource.getConfiguration();
        String queueName = configuration.jobName();
        return queueService.getQueue(queueName);
    }

    private List<M> handleQueueMessage(EventSource eventSource, M queueMessage) throws Throwable {
        WorkerHandler<M> workerHandler = workerHandlerService.getWorkerHandler();
        return workerHandler.handleQueueMessage(eventSource.getConfiguration(), queueMessage);
    }

    private void updateQueue(Q queue, M queueMessage, List<M> newMessages) throws QueueOperationFailedException {
        queue.removeItem(queueMessage);
        queue.addItems(newMessages);
    }

    private void handleEmptyQueueException(EventSource eventSource, Q queue) {
        notifyCompletion(eventSource);
        queueService.removeQueue(queue);
    }

    private void notifyCompletion(EventSource eventSource) {
        Notifier notifier = getNotifier();
        NotifierConfiguration notifierConfiguration = getNotifierConfiguration(eventSource);
        notifier.notifyCompletion(notifierConfiguration);
    }

    private void handleMessageConverterException(EventSource eventSource, MessageConverterException e) {
        notifyError(eventSource, e);
    }

    private void handleRateLimitEncounteredException(EventSource<C> eventSource, RateLimitEncounteredException e)
            throws WorkerOperationFailedException {

        rescheduleForLater(eventSource.getConfiguration(), systemClock.getDifferenceInMinutesFromNow(e.getResetDate()));
    }

    @Override
    public void rescheduleForLater(C configuration, long minutesFromNow) throws WorkerOperationFailedException {
        String workerName = workerService.getWorkerName();
        A alarm = alarmService.createAlarm(configuration, minutesFromNow, workerName);
        try {
            alarmService.postAlarm(alarm);
        } catch (AlarmOperationFailedException e) {
            throw new WorkerOperationFailedException("rescheduleForLater", e);
        }
    }

    private void handleQueueOperationFailedException(EventSource eventSource, QueueOperationFailedException e) {
        notifyError(eventSource, e);
        rescheduleImmediately(eventSource.getConfiguration());
    }

    @Override
    public void rescheduleImmediately(Configuration configuration) {
        workerService.startWorker(configuration);
    }

    private void handleAnyOtherException(EventSource eventSource, Q queue, Throwable t) {
        queue.purgeQueue();
        queueService.removeQueue(queue);
        notifyError(eventSource, t);
    }

    private void notifyError(EventSource eventSource, Throwable t) {
        t.printStackTrace();

        Notifier notifier = getNotifier();
        NotifierConfiguration notifierConfiguration = getNotifierConfiguration(eventSource);
        notifier.notifyError(notifierConfiguration, t);
    }

    private Notifier getNotifier() {
        return notifierService.getNotifier();
    }

    private NotifierConfiguration getNotifierConfiguration(EventSource eventSource) {
        return eventSource.getConfiguration().notifierConfiguration();
    }
}
