package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.alarm.Alarm;
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

import java.time.Instant;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class BasicWorkerTest {

    private static final String ANY_QUEUE_JOB_NAME = "some_queue";
    private static final String ANY_ALARM_NAME = "some_alarm";
    private static final String ANY_WORKER_DESCRIPTOR = "some_lambda_reference";
    private static final Boolean HAS_ALARM = true;

    @Mock
    private WorkerService workerService;

    @Mock
    private AlarmService<Alarm, Configuration<NotifierConfiguration>> alarmService;

    @Mock
    private QueueService<Queue<QueueMessage>> queueService;

    @Mock
    private NotifierService notifierService;

    @Mock
    private WorkerHandlerService workerHandlerService;

    @Mock
    private SystemClock systemClock;

    @InjectMocks
    private BasicWorker<Alarm, QueueMessage, Queue<QueueMessage>, NotifierConfiguration, Configuration<NotifierConfiguration>> worker;

    @Mock
    private Configuration<NotifierConfiguration> configuration;

    @Mock
    private Alarm alarm;

    @Mock
    private Notifier<NotifierConfiguration, Configuration<NotifierConfiguration>> notifier;

    @Mock
    private WorkerHandler<QueueMessage> workerHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(configuration.jobName()).thenReturn(ANY_QUEUE_JOB_NAME);

        Mockito.when(alarm.getName()).thenReturn(ANY_ALARM_NAME);

        Mockito.when(notifierService.getNotifier()).thenReturn(notifier);
        Mockito.when(workerHandlerService.getWorkerHandler()).thenReturn(workerHandler);
    }

    @Test
    public void givenStartedFromAlarm_whenDoWork_thenRemoveAlarm()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException {

        givenStartedFromAlarm();
        givenAnyQueue();

        worker.doWork(configuration);

        Mockito.verify(alarmService).removeAlarm(configuration.alarmName());
    }

    private void givenStartedFromAlarm() {
        Mockito.when(configuration.alarmName()).thenReturn(ANY_ALARM_NAME);
        Mockito.when(configuration.hasAlarm()).thenReturn(HAS_ALARM);
    }

    @Test
    public void givenStartedFromNonAlarm_whenDoWork_thenDoNotRemoveAlarm()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException {

        givenAnyQueue();

        worker.doWork(configuration);

        Mockito.verify(alarmService, Mockito.never()).removeAlarm(Matchers.any(Alarm.class));
    }

    @Test
    public void givenEmptyQueue_whenDoWork_thenNotifyCompletionAndDeleteQueueAndDoNotReschedule()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException, NotifierOperationFailedException {

        Queue<QueueMessage> queue = givenEmptyQueue();

        worker.doWork(configuration);

        Mockito.verify(notifier).notifyCompletion(configuration);
        Mockito.verify(queueService).removeQueue(queue);
        verifyNoErrorNotified();
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenIncompatibleMessageInQueue_whenDoWork_thenNotifyConversionErrorAndDoNotReschedule()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException, NotifierOperationFailedException {

        givenInvalidMessagesQueue();

        worker.doWork(configuration);

        verifyErrorNotified(MessageConverterException.class);
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenValidQueue_whenDoWork_thenDelegateToHandleQueueMessage() throws Throwable {
        givenAnyQueue();

        worker.doWork(configuration);

        Mockito.verify(workerHandler).handleQueueMessage(Matchers.eq(configuration), Matchers.any(QueueMessage.class));
    }

    @Test
    public void givenValidQueueAndRateLimitExpired_whenDoWork_thenRescheduleForLater() throws Throwable {
        givenAnyQueue();
        Instant nextResetInstant = Instant.now().plusSeconds(600);
        Date nextResetDate = new Date(nextResetInstant.toEpochMilli());
        RateLimitEncounteredException rateLimitEncounteredException = new RateLimitEncounteredException("YOLO", nextResetDate);
        Mockito.when(workerHandler.handleQueueMessage(Matchers.eq(configuration), Matchers.any(QueueMessage.class)))
                .thenThrow(rateLimitEncounteredException);
        Mockito.when(workerService.getWorkerName()).thenReturn(ANY_WORKER_DESCRIPTOR);
        Mockito.when(alarmService.createNewAlarm(Matchers.anyLong(), Matchers.eq(ANY_QUEUE_JOB_NAME), Matchers.eq(ANY_WORKER_DESCRIPTOR)))
                .thenReturn(alarm);

        worker.doWork(configuration);

        Mockito.verify(alarmService).createNewAlarm(Matchers.anyLong(), Matchers.eq(ANY_QUEUE_JOB_NAME), Matchers.eq(ANY_WORKER_DESCRIPTOR));
        Mockito.verify(alarmService).postAlarm(Matchers.any(Alarm.class), AdditionalMatchers.not(Matchers.eq(configuration)));
        verifyNoErrorNotified();
    }

    @Test
    public void givenAnyQueueAndErroringWorkerHandler_whenDoWork_thenPurgeDeleteQueueAndNotifyErrorAndDoNotReschedule() throws Throwable {

        Queue<QueueMessage> queue = givenAnyQueue();
        Mockito.when(workerHandler.handleQueueMessage(Matchers.eq(configuration), Matchers.any(QueueMessage.class))).thenThrow(Exception.class);

        worker.doWork(configuration);

        Mockito.verify(queue).purgeQueue();
        Mockito.verify(queueService).removeQueue(queue);
        verifyErrorNotified(Exception.class);
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenFailingQueueAddItems_whenDoWork_thenNotifyErrorAndReschedule()
            throws EmptyQueueException, MessageConverterException, QueueOperationFailedException, WorkerOperationFailedException,
            NotifierOperationFailedException {

        Queue<QueueMessage> queue = givenAnyQueue();
        Mockito.when(queue.addItems(Matchers.anyListOf(QueueMessage.class))).thenThrow(QueueOperationFailedException.class);

        worker.doWork(configuration);

        verifyErrorNotified(QueueOperationFailedException.class);
    }

    @Test
    public void givenAnyQueue_whenDoWork_thenRescheduleImmediately()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException {

        givenAnyQueue();

        worker.doWork(configuration);

        verifyRescheduleImmediately();
    }

    private Queue<QueueMessage> givenAnyQueue() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> anyQueue = Mockito.mock(DefaultQueue.class);
        QueueMessage queueMessage = Mockito.mock(QueueMessage.class);
        Mockito.when(anyQueue.getItem()).thenReturn(queueMessage);
        return givenQueue(anyQueue);
    }

    private Queue<QueueMessage> givenEmptyQueue() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> emptyQueue = Mockito.mock(DefaultQueue.class);
        Mockito.when(emptyQueue.getItem()).thenThrow(EmptyQueueException.class);
        return givenQueue(emptyQueue);
    }

    private Queue<QueueMessage> givenInvalidMessagesQueue() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> emptyQueue = Mockito.mock(DefaultQueue.class);
        Mockito.when(emptyQueue.getItem()).thenThrow(MessageConverterException.class);
        return givenQueue(emptyQueue);
    }

    private Queue<QueueMessage> givenQueue(Queue<QueueMessage> queue) {
        Mockito.when(queueService.getQueue(ANY_QUEUE_JOB_NAME)).thenReturn(queue);
        return queue;
    }

    private void verifyNoErrorNotified() throws NotifierOperationFailedException {
        Mockito.verify(notifier, Mockito.never()).notifyError(Matchers.any(DefaultConfiguration.class), Matchers.any(Exception.class));
    }

    private void verifyNoRescheduleImmediately() {
        Mockito.verify(workerService, Mockito.never()).startWorker(configuration);
    }

    private <T extends Exception> void verifyErrorNotified(Class<T> exception) throws NotifierOperationFailedException {
        Mockito.verify(notifier).notifyError(Matchers.any(DefaultConfiguration.class), Matchers.any(exception));
    }

    private void verifyRescheduleImmediately() {
        Mockito.verify(workerService).startWorker(AdditionalMatchers.not(Matchers.eq(configuration)));
    }

}
