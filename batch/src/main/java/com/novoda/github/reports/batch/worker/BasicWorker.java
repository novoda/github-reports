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
    private final Logger logger;
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
                                                                               WorkerHandlerService<M> workerHandlerService,
                                                                               Logger logger) {
        SystemClock systemClock = SystemClock.newInstance();
        return new BasicWorker<>(workerService, alarmService, queueService, notifierService, workerHandlerService, logger, systemClock);
    }

    private BasicWorker(WorkerService<C> workerService,
                        AlarmService<A, C> alarmService,
                        QueueService<Q> queueService,
                        NotifierService<N, C> notifierService,
                        WorkerHandlerService<M> workerHandlerService,
                        Logger logger,
                        SystemClock systemClock) {

        this.workerService = workerService;
        this.alarmService = alarmService;
        this.queueService = queueService;
        this.notifierService = notifierService;
        this.workerHandlerService = workerHandlerService;
        this.logger = logger;
        this.systemClock = systemClock;
    }

    @Override
    public void doWork(C configuration) throws WorkerOperationFailedException {
        if (configuration.hasAlarm()) {
            String alarmName = configuration.alarmName();
            logger.log("The input configuration has an alarm with name \"%s\".", alarmName);
            alarmService.removeAlarm(alarmName);
        }

        Q queue = null;
        try {
            queue = getQueue(configuration);
            M queueMessage = getItem(queue);
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

        logger.log("COMPLETED.");
    }

    private Q getQueue(C configuration) {
        String queueName = configuration.jobName();
        return queueService.getQueue(queueName);
    }

    private M getItem(Q queue) throws EmptyQueueException, MessageConverterException {
        return queue.getItem();
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
        logger.log("It looks like the queue is empty.");

        notifyCompletion(configuration);

        removeQueue(queueService, queue);
    }

    private void notifyCompletion(C configuration) {
        Notifier<N, C> notifier = getNotifier();
        try {
            notifier.notifyCompletion(configuration);
        } catch (NotifierOperationFailedException e) {
            logger.log("Could not notify the completion because of error:\n%s", e);
            e.printStackTrace();
        }
    }

    private void handleMessageConverterException(C configuration, MessageConverterException e) {
        logger.log("Error while converting the message from the queue:\n%s", e);
        notifyError(configuration, e);
    }

    private void handleRateLimitEncounteredException(C configuration, RateLimitEncounteredException e)
            throws WorkerOperationFailedException {

        logger.log("Rate limit encountered:\n%s", e);

        long differenceInMinutesFromNow = systemClock.getDifferenceInMinutesFromNow(e.getResetDate());
        rescheduleForLater(configuration, differenceInMinutesFromNow);
    }

    @Override
    public void rescheduleForLater(C configuration, long minutesFromNow) throws WorkerOperationFailedException {
        logger.log("Rescheduling in %d minutes...", minutesFromNow);
        String workerName = workerService.getWorkerName();
        A alarm = alarmService.createNewAlarm(minutesFromNow, configuration.jobName(), workerName);
        configuration = withNewAlarmIntoConfiguration(alarm, configuration);
        try {
            alarmService.postAlarm(alarm, configuration);
        } catch (AlarmOperationFailedException e) {
            throw new WorkerOperationFailedException("rescheduleForLater", e);
        }
        logger.log("Rescheduled in %d minutes.", minutesFromNow);
    }

    private C withNewAlarmIntoConfiguration(A alarm, C configuration) {
        return configuration.withAlarmName(alarm.getName());
    }

    private void handleQueueOperationFailedException(C configuration, Q queue, QueueOperationFailedException e) {
        logger.log("A queue operation failed:\n%s", e);

        notifyError(configuration, e);
        try {
            rescheduleImmediately(configuration);
        } catch (WorkerStartException cantRescheduleException) {
            handleAnyOtherException(configuration, queue, cantRescheduleException);
        }
    }

    @Override
    public void rescheduleImmediately(C configuration) throws WorkerStartException {
        logger.log("Restarting this worker...");
        configuration = configuration.withNoAlarmName();
        workerService.startWorker(configuration);
    }

    private void handleAnyOtherException(C configuration, Q queue, Throwable t) {
        logger.log("There was an unhandled error which terminated the job:\n%s", t);

        if (queue != null) {
            queue.purgeQueue();
            removeQueue(queueService, queue);
        }
        notifyError(configuration, t);
    }

    private void removeQueue(QueueService<Q> queueService, Q queue) {
        queueService.removeQueue(queue);
    }

    private void notifyError(C configuration, Throwable t) {
        Notifier<N, C> notifier = getNotifier();
        try {
            t.printStackTrace();
            notifier.notifyError(configuration, t);
        } catch (NotifierOperationFailedException e) {
            logger.log("During the error notification another error occurred:\n%s", e);
            e.printStackTrace();
        }
    }

    private Notifier<N, C> getNotifier() {
        return notifierService.getNotifier();
    }
}
