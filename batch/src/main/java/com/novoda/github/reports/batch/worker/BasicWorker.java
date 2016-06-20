package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.alarm.Alarm;
import com.novoda.github.reports.batch.alarm.AlarmOperationFailedException;
import com.novoda.github.reports.batch.alarm.AlarmService;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierOperationFailedException;
import com.novoda.github.reports.batch.notifier.NotifierService;
import com.novoda.github.reports.batch.queue.EmptyQueueException;
import com.novoda.github.reports.batch.queue.MessageConverterException;
import com.novoda.github.reports.batch.queue.Queue;
import com.novoda.github.reports.batch.queue.QueueMessage;
import com.novoda.github.reports.batch.queue.QueueOperationFailedException;
import com.novoda.github.reports.batch.queue.QueueService;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;
import com.novoda.github.reports.util.SystemClock;

import java.util.List;

public class BasicWorker<
        A extends Alarm,
        M extends QueueMessage,
        Q extends Queue<M>,
        N extends NotifierConfiguration,
        C extends Configuration<N>>
        implements Worker<N, C> {

    private final WorkerService<C> workerService;
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
            C extends Configuration<N>> BasicWorker<A, M, Q, N, C> newInstance(WorkerService<C> workerService,
                                                                               AlarmService<A, C> alarmService,
                                                                               QueueService<Q> queueService,
                                                                               NotifierService<N, C> notifierService,
                                                                               WorkerHandlerService<M> workerHandlerService) {
        SystemClock systemClock = SystemClock.newInstance();
        return new BasicWorker<>(workerService, alarmService, queueService, notifierService, workerHandlerService, systemClock);
    }

    private BasicWorker(WorkerService<C> workerService,
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
    public void doWork(C configuration) throws WorkerOperationFailedException {
        if (configuration.hasAlarm()) {
            String alarmName = configuration.alarmName();
            alarmService.removeAlarm(alarmName);
        }

        Q queue = getQueue(configuration);

        try {
            M queueMessage = queue.getItem();
            List<M> newMessages = handleQueueMessage(configuration, queueMessage);
            updateQueue(queue, queueMessage, newMessages);
            rescheduleImmediately(configuration);
        } catch (EmptyQueueException emptyQueue) {
            handleEmptyQueueException(configuration, queue);
        } catch (MessageConverterException e) {
            handleMessageConverterException(configuration, e);
        } catch (RateLimitEncounteredException e) {
            handleRateLimitEncounteredException(configuration, e);
        } catch (QueueOperationFailedException e) {
            handleQueueOperationFailedException(configuration, queue, e);
        } catch (Throwable t) {
            handleAnyOtherException(configuration, queue, t);
        }
    }

    private Q getQueue(C configuration) {
        String queueName = configuration.jobName();
        return queueService.getQueue(queueName);
    }

    private List<M> handleQueueMessage(C configuration, M queueMessage) throws Throwable {
        WorkerHandler<M> workerHandler = workerHandlerService.getWorkerHandler();
        return workerHandler.handleQueueMessage(configuration, queueMessage);
    }

    private void updateQueue(Q queue, M queueMessage, List<M> newMessages) throws QueueOperationFailedException {
        queue.removeItem(queueMessage);
        queue.addItems(newMessages);
    }

    private void handleEmptyQueueException(C configuration, Q queue) {
        notifyCompletion(configuration);
        queueService.removeQueue(queue);
    }

    private void notifyCompletion(C configuration) {
        Notifier<N, C> notifier = getNotifier();
        try {
            notifier.notifyCompletion(configuration);
        } catch (NotifierOperationFailedException e) {
            e.printStackTrace();
        }
    }

    private void handleMessageConverterException(C configuration, MessageConverterException e) {
        notifyError(configuration, e);
    }

    private void handleRateLimitEncounteredException(C configuration, RateLimitEncounteredException e)
            throws WorkerOperationFailedException {

        rescheduleForLater(configuration, systemClock.getDifferenceInMinutesFromNow(e.getResetDate()));
    }

    @Override
    public void rescheduleForLater(C configuration, long minutesFromNow) throws WorkerOperationFailedException {
        String workerName = workerService.getWorkerName();
        A alarm = alarmService.createNewAlarm(minutesFromNow, configuration.jobName(), workerName);
        configuration = withNewAlarmIntoConfiguration(alarm, configuration);
        try {
            alarmService.postAlarm(alarm, configuration);
        } catch (AlarmOperationFailedException e) {
            throw new WorkerOperationFailedException("rescheduleForLater", e);
        }
    }

    private C withNewAlarmIntoConfiguration(A alarm, C configuration) {
        return configuration.withAlarmName(alarm.getName());
    }

    private void handleQueueOperationFailedException(C configuration, Q queue, QueueOperationFailedException e) {
        notifyError(configuration, e);
        try {
            rescheduleImmediately(configuration);
        } catch (WorkerStartException cantRescheduleException) {
            handleAnyOtherException(configuration, queue, cantRescheduleException);
        }
    }

    @Override
    public void rescheduleImmediately(C configuration) throws WorkerStartException {
        configuration = configuration.withNoAlarmName();
        workerService.startWorker(configuration);
    }

    private void handleAnyOtherException(C configuration, Q queue, Throwable t) {
        queue.purgeQueue();
        queueService.removeQueue(queue);
        notifyError(configuration, t);
    }

    private void notifyError(C configuration, Throwable t) {
        t.printStackTrace();

        Notifier<N, C> notifier = getNotifier();
        try {
            notifier.notifyError(configuration, t);
        } catch (NotifierOperationFailedException e) {
            e.printStackTrace();
        }
    }

    private Notifier<N, C> getNotifier() {
        return notifierService.getNotifier();
    }
}
