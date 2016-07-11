package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.alarm.Alarm;
import com.novoda.github.reports.batch.alarm.AlarmOperationFailedException;
import com.novoda.github.reports.batch.alarm.AlarmService;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;
import com.novoda.github.reports.batch.logger.DefaultLogger;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.logger.LoggerHandler;
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
import com.novoda.github.reports.util.StringHelper;
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

    private boolean hasRescheduled;

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
                                                                               LoggerHandler loggerHandler) {

        SystemClock systemClock = SystemClock.newInstance();
        DefaultLogger logger = DefaultLogger.newInstance(loggerHandler);
        return new BasicWorker<>(workerService,
                                 alarmService,
                                 queueService,
                                 notifierService,
                                 workerHandlerService,
                                 logger,
                                 systemClock);
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

        this.hasRescheduled = false;
    }

    @Override
    public void doWork(C configuration) throws WorkerOperationFailedException {
        if (configuration.hasAlarm()) {
            String alarmName = configuration.alarmName();
            logger.info("The input configuration has an alarm with name \"%s\".", alarmName);
            alarmService.removeAlarm(alarmName);
        }

        try {
            doWorkAndHandleErrors(configuration);
        } catch (Throwable throwable) {
            handleAnyOtherException(configuration, throwable);
        }
    }

    private void doWorkAndHandleErrors(C configuration) throws
            NotifierOperationFailedException,
            WorkerOperationFailedException,
            WorkerStartException {

        Q queue = null;

        try {
            queue = getQueue(configuration);
            M queueMessage = getItem(queue);
            List<M> newMessages = handleQueueMessage(configuration, queueMessage);
            updateQueue(queue, queueMessage, newMessages);
            rescheduleImmediately(configuration);
        } catch (EmptyQueueException emptyQueue) {
            handleEmptyQueueException(configuration, queue);
        } catch (MessageConverterException messageConversionError) {
            handleMessageConverterException(configuration, messageConversionError);
        } catch (RateLimitEncounteredException rateLimitError) {
            handleRateLimitEncounteredException(configuration, rateLimitError);
        } catch (QueueOperationFailedException queueOperationFail) {
            handleQueueOperationFailedException(configuration, queueOperationFail);
        } catch (TemporaryNetworkException networkOperationFail) {
            handleNetworkOperationFailedException(configuration, networkOperationFail);
        } catch (Throwable anyOtherError) {
            handleAnyOtherException(configuration, anyOtherError);
        }
    }

    private Q getQueue(C configuration) {
        String queueName = configuration.jobName();
        return queueService.getQueue(queueName);
    }

    private M getItem(Q queue) throws EmptyQueueException, MessageConverterException {
        return queue.getItem();
    }

    private List<M> handleQueueMessage(C configuration, M queueMessage) throws Throwable {
        logger.info("Handling message: %s...", queueMessage.toShortString());

        WorkerHandler<M> workerHandler = workerHandlerService.getWorkerHandler();
        List<M> nextMessages = workerHandler.handleQueueMessage(configuration, queueMessage);

        logger.info("Message handled. %d new messages have been generated.", nextMessages.size());

        return nextMessages;
    }

    private void updateQueue(Q queue, M queueMessage, List<M> newMessages) throws QueueOperationFailedException {
        queue.removeItem(queueMessage);
        queue.addItems(newMessages);
    }

    private void handleEmptyQueueException(C configuration, Q queue) throws NotifierOperationFailedException {
        logger.debug("It looks like the queue is empty.");
        logger.info(StringHelper.emojiToString(0x2705) + "  Job completed successfully!");

        notifyCompletion(configuration);
        logger.info("Completion notified.");

        removeQueue(queueService, queue);
    }

    private void notifyCompletion(C configuration) throws NotifierOperationFailedException {
        Notifier<N, C> notifier = getNotifier();
        notifier.notifyCompletion(configuration);
    }

    private void removeQueue(QueueService<Q> queueService, Q queue) {
        queueService.removeQueue(queue);
    }

    private void handleMessageConverterException(C configuration, MessageConverterException e) throws NotifierOperationFailedException {
        logger.error("Error while converting the message from the queue:\n%s", e);
        notifyError(configuration, e);
    }

    private void handleRateLimitEncounteredException(C configuration, RateLimitEncounteredException e)
            throws WorkerOperationFailedException {

        logger.warn("Rate limit encountered:\n%s", e);

        long differenceInMinutesFromNow = systemClock.getDifferenceInMinutesFromNow(e.getResetDate());
        rescheduleForLater(configuration, differenceInMinutesFromNow);
    }

    @Override
    public void rescheduleForLater(C configuration, long minutesFromNow) throws WorkerOperationFailedException {
        logger.info("Rescheduling in %d minutes...", minutesFromNow);
        String workerName = workerService.getWorkerName();
        A alarm = alarmService.createNewAlarm(minutesFromNow, configuration.jobName(), workerName);
        configuration = withNewAlarmIntoConfiguration(alarm, configuration);
        try {
            alarmService.postAlarm(alarm, configuration);
        } catch (AlarmOperationFailedException e) {
            throw new WorkerOperationFailedException("rescheduleForLater", e);
        }
        logger.info("Rescheduled in %d minutes.", minutesFromNow);
    }

    private C withNewAlarmIntoConfiguration(A alarm, C configuration) {
        return configuration.withAlarmName(alarm.getName());
    }

    private void handleQueueOperationFailedException(C configuration, QueueOperationFailedException e)
            throws WorkerStartException, NotifierOperationFailedException {

        logger.error("A queue operation failed:\n%s", e);

        notifyError(configuration, e);
        rescheduleImmediately(configuration);
    }

    private void handleNetworkOperationFailedException(C configuration, TemporaryNetworkException e)
            throws WorkerStartException, NotifierOperationFailedException {

        logger.warn("A network operation failed:\n%s", e);

        rescheduleImmediately(configuration);
    }

    @Override
    public void rescheduleImmediately(C configuration) throws WorkerStartException {
        if (hasRescheduled) {
            throw new WorkerStartException("The worker was already restarted, this shouldn't be possible!");
        }

        hasRescheduled = true;
        configuration = configuration.withNoAlarmName();
        workerService.startWorker(configuration);

        logger.info("New worker instance started.");
    }

    private void handleAnyOtherException(C configuration, Throwable throwable) {
        logger.error("There was an unhandled error which terminated the job:\n%s", throwable);

        try {
            notifyError(configuration, throwable);
        } catch (Throwable notificationError) {
            notificationError.printStackTrace();
        }
    }

    private void notifyError(C configuration, Throwable t) throws NotifierOperationFailedException {
        Notifier<N, C> notifier = getNotifier();
        t.printStackTrace();
        notifier.notifyError(configuration, t);
    }

    private Notifier<N, C> getNotifier() {
        return notifierService.getNotifier();
    }
}
