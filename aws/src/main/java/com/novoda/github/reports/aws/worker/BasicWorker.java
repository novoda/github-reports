package com.novoda.github.reports.aws.worker;

import com.novoda.github.reports.aws.alarm.Alarm;
import com.novoda.github.reports.aws.alarm.AlarmOperationFailedException;
import com.novoda.github.reports.aws.alarm.AlarmService;
import com.novoda.github.reports.aws.configuration.Configuration;
import com.novoda.github.reports.aws.configuration.NotifierConfiguration;
import com.novoda.github.reports.aws.notifier.Notifier;
import com.novoda.github.reports.aws.notifier.NotifierOperationFailedException;
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
        N extends NotifierConfiguration,
        C extends Configuration<N>>
        implements Worker<N, C> {

    private final WorkerService workerService;
    private final AlarmService<A, C> alarmService;
    private final QueueService<Q> queueService;
    private final NotifierService<N, C> notifierService;
    private final WorkerHandlerService<M> workerHandlerService;
    private final SystemClock systemClock;

    public static <
            A extends Alarm,
            M extends QueueMessage,
            Q extends Queue<M>,
            N extends NotifierConfiguration,
            C extends Configuration<N>> BasicWorker<A, M, Q, N, C> newInstance(WorkerService workerService,
                                                                               AlarmService<A, C> alarmService,
                                                                               QueueService<Q> queueService,
                                                                               NotifierService<N, C> notifierService,
                                                                               WorkerHandlerService<M> workerHandlerService) {
        SystemClock systemClock = SystemClock.newInstance();
        return new BasicWorker<>(workerService, alarmService, queueService, notifierService, workerHandlerService, systemClock);
    }

    private BasicWorker(WorkerService workerService,
                        AlarmService<A, C> alarmService,
                        QueueService<Q> queueService,
                        NotifierService<N, C> notifierService,
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
    public void doWork(EventSource<N, C> eventSource) throws WorkerOperationFailedException {
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
        } catch (Exception e) {
            handleAnyOtherException(eventSource, queue, e);
        }
    }

    private Q getQueue(EventSource<N, C> eventSource) {
        Configuration configuration = eventSource.getConfiguration();
        String queueName = configuration.jobName();
        return queueService.getQueue(queueName);
    }

    private List<M> handleQueueMessage(EventSource<N, C> eventSource, M queueMessage) throws Exception, RateLimitEncounteredException {
        WorkerHandler<M> workerHandler = workerHandlerService.getWorkerHandler();
        return workerHandler.handleQueueMessage(eventSource.getConfiguration(), queueMessage);
    }

    private void updateQueue(Q queue, M queueMessage, List<M> newMessages) throws QueueOperationFailedException {
        queue.removeItem(queueMessage);
        queue.addItems(newMessages);
    }

    private void handleEmptyQueueException(EventSource<N, C> eventSource, Q queue) {
        notifyCompletion(eventSource);
        queueService.removeQueue(queue);
    }

    private void notifyCompletion(EventSource<N, C> eventSource) {
        Notifier<N, C> notifier = getNotifier();
        C configuration = eventSource.getConfiguration();
        try {
            notifier.notifyCompletion(configuration);
        } catch (NotifierOperationFailedException e) {
            e.printStackTrace();
        }
    }

    private void handleMessageConverterException(EventSource<N, C> eventSource, MessageConverterException e) {
        notifyError(eventSource, e);
    }

    private void handleRateLimitEncounteredException(EventSource<N, C> eventSource, RateLimitEncounteredException e)
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

    private void handleQueueOperationFailedException(EventSource<N, C> eventSource, QueueOperationFailedException e) {
        notifyError(eventSource, e);
        rescheduleImmediately(eventSource.getConfiguration());
    }

    @Override
    public void rescheduleImmediately(C configuration) {
        workerService.startWorker(configuration);
    }

    private void handleAnyOtherException(EventSource<N, C> eventSource, Q queue, Exception e) {
        queue.purgeQueue();
        queueService.removeQueue(queue);
        notifyError(eventSource, e);
    }

    private void notifyError(EventSource<N, C> eventSource, Exception exception) {
        exception.printStackTrace();

        Notifier<N, C> notifier = getNotifier();
        C configuration = eventSource.getConfiguration();
        try {
            notifier.notifyError(configuration, exception);
        } catch (NotifierOperationFailedException e) {
            e.printStackTrace();
        }
    }

    private Notifier<N, C> getNotifier() {
        return notifierService.getNotifier();
    }
}
