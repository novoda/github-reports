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
            logger.log("The input configuration has an alarm with name \"%s\" that will be removed...", alarmName);
            alarmService.removeAlarm(alarmName);
            logger.log("The alarm \"%s\" has been removed.", alarmName);
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
        logger.log("Getting queue with name \"%s\"...", queueName);
        Q queue = queueService.getQueue(queueName);
        logger.log("Got queue with name \"%s\".", queueName);
        return queue;
    }

    private M getItem(Q queue) throws EmptyQueueException, MessageConverterException {
        logger.log("Getting first item from queue...");
        M item = queue.getItem();
        logger.log("Got item.");
        return item;
    }

    private List<M> handleQueueMessage(C configuration, M queueMessage) throws Throwable {
        logger.log("Getting worker handler...");
        WorkerHandler<M> workerHandler = workerHandlerService.getWorkerHandler();
        logger.log("Got worker handler.");

        logger.log("Handling the message...");
        List<M> nextMessages = workerHandler.handleQueueMessage(configuration, queueMessage);
        logger.log("Message handled. %d new messages have been generated", nextMessages.size());
        return nextMessages;
    }

    private void updateQueue(Q queue, M queueMessage, List<M> newMessages) throws QueueOperationFailedException {
        logger.log("Removing first item from the queue...");
        queue.removeItem(queueMessage);
        logger.log("Removed first item from the queue.");

        int size = newMessages.size();
        logger.log("Adding %d new items in the queue...", size);
        queue.addItems(newMessages);
        logger.log("Added %d new items in the queue.", size);
    }

    private void handleEmptyQueueException(C configuration, Q queue) {
        logger.log("It looks like the queue is empty.");

        notifyCompletion(configuration);

        removeQueue(queueService, queue);
    }

    private void notifyCompletion(C configuration) {
        logger.log("Notifying completion...");
        Notifier<N, C> notifier = getNotifier();
        try {
            notifier.notifyCompletion(configuration);
        } catch (NotifierOperationFailedException e) {
            logger.log("Could not notify the completion.");
            logger.log(e);
            e.printStackTrace();
        }
        logger.log("Completion notified.");
    }

    private void handleMessageConverterException(C configuration, MessageConverterException e) {
        logger.log("Error while converting the message from the queue.");
        logger.log(e);
        notifyError(configuration, e);
    }

    private void handleRateLimitEncounteredException(C configuration, RateLimitEncounteredException e)
            throws WorkerOperationFailedException {

        logger.log("Rate limit encountered.");
        logger.log(e);

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
        logger.log("A queue operation failed.");
        logger.log(e);

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
        logger.log("There was an unhandled error which terminated the job.");
        logger.log(t);

        if (queue != null) {
            logger.log("Purging the queue...");
            queue.purgeQueue();
            logger.log("Queue purged.");
            removeQueue(queueService, queue);
        }
        notifyError(configuration, t);
    }

    private void removeQueue(QueueService<Q> queueService, Q queue) {
        logger.log("Deleting queue...");
        queueService.removeQueue(queue);
        logger.log("Deleted queue.");
    }

    private void notifyError(C configuration, Throwable t) {
        logger.log("Notifying error...");
        t.printStackTrace();

        Notifier<N, C> notifier = getNotifier();
        try {
            notifier.notifyError(configuration, t);
        } catch (NotifierOperationFailedException e) {
            logger.log("Could not notify the error.");
            logger.log(e);
            e.printStackTrace();
        }
        logger.log("Error notified.");
    }

    private Notifier<N, C> getNotifier() {
        return notifierService.getNotifier();
    }
}
