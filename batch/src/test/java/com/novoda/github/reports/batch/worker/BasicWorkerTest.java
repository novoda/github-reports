package com.novoda.github.reports.batch.worker;

import com.novoda.github.reports.batch.MessageNotSupportedException;
import com.novoda.github.reports.batch.alarm.Alarm;
import com.novoda.github.reports.batch.alarm.AlarmService;
import com.novoda.github.reports.batch.configuration.Configuration;
import com.novoda.github.reports.batch.configuration.NotifierConfiguration;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierOperationFailedException;
import com.novoda.github.reports.batch.notifier.NotifierService;
import com.novoda.github.reports.batch.queue.*;
import com.novoda.github.reports.service.network.RateLimitEncounteredException;
import com.novoda.github.reports.util.SystemClock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.SocketTimeoutException;
import java.time.Instant;
import java.util.Date;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isA;

public class BasicWorkerTest {

    private static final String ANY_QUEUE_JOB_NAME = "some_queue";
    private static final String ANY_ALARM_NAME = "some_alarm";
    private static final String ANY_WORKER_DESCRIPTOR = "some_lambda_reference";
    private static final Boolean HAS_ALARM = true;

    @Mock
    private WorkerService<Configuration<NotifierConfiguration>> workerService;

    @Mock
    private AlarmService<Alarm, Configuration<NotifierConfiguration>> alarmService;

    @Mock
    private QueueService<Queue<QueueMessage>> queueService;

    @Mock
    private NotifierService notifierService;

    @Mock
    private WorkerHandlerService workerHandlerService;

    @Mock
    private Logger logger;

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

        when(configuration.jobName()).thenReturn(ANY_QUEUE_JOB_NAME);

        when(alarm.getName()).thenReturn(ANY_ALARM_NAME);

        when(notifierService.getNotifier()).thenReturn(notifier);
        when(workerHandlerService.getWorkerHandler()).thenReturn(workerHandler);
    }

    @Test
    public void givenStartedFromAlarm_whenDoWork_thenRemoveAlarm()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException {

        givenStartedFromAlarm();
        givenAnyQueue();

        worker.doWork(configuration);

        verify(alarmService).removeAlarm(configuration.alarmName());
    }

    private void givenStartedFromAlarm() {
        when(configuration.alarmName()).thenReturn(ANY_ALARM_NAME);
        when(configuration.hasAlarm()).thenReturn(HAS_ALARM);
    }

    @Test
    public void givenStartedFromNonAlarm_whenDoWork_thenDoNotRemoveAlarm()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException {

        givenAnyQueue();

        worker.doWork(configuration);

        verify(alarmService, never()).removeAlarm(any(Alarm.class));
    }

    @Test
    public void givenEmptyQueue_whenDoWork_thenNotifyCompletionAndNoErrors() throws
            EmptyQueueException,
            MessageConverterException,
            WorkerOperationFailedException,
            NotifierOperationFailedException {

        givenEmptyQueue();

        worker.doWork(configuration);

        verify(notifier).notifyCompletion(configuration);
        verifyNoErrorNotified();
    }

    @Test
    public void givenEmptyQueue_whenDoWork_thenDeleteQueue() throws
            EmptyQueueException,
            MessageConverterException,
            WorkerOperationFailedException,
            NotifierOperationFailedException,
            WorkerStartException {

        Queue<QueueMessage> queue = givenEmptyQueue();

        worker.doWork(configuration);

        verify(queueService).removeQueue(queue);
    }

    @Test
    public void givenEmptyQueue_whenDoWork_thenDoNotReschedule() throws
            EmptyQueueException,
            MessageConverterException,
            WorkerOperationFailedException,
            NotifierOperationFailedException,
            WorkerStartException {

        givenEmptyQueue();

        worker.doWork(configuration);

        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenIncompatibleMessageInQueue_whenDoWork_thenNotifyConversionErrorAndDoNotReschedule() throws
            EmptyQueueException,
            MessageConverterException,
            WorkerOperationFailedException,
            NotifierOperationFailedException,
            WorkerStartException {

        givenInvalidMessagesQueue();

        worker.doWork(configuration);

        verifyErrorNotified(MessageConverterException.class);
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenValidQueue_whenDoWork_thenDelegateToHandleQueueMessage() throws Throwable {
        givenAnyQueue();

        worker.doWork(configuration);

        verify(workerHandler).handleQueueMessage(eq(configuration), any(QueueMessage.class));
    }

    @Test
    public void givenValidQueueAndRateLimitExpired_whenDoWork_thenRescheduleForLater() throws Throwable {
        givenAnyQueue();
        Instant nextResetInstant = Instant.now().plusSeconds(600);
        Date nextResetDate = new Date(nextResetInstant.toEpochMilli());
        RateLimitEncounteredException rateLimitEncounteredException = new RateLimitEncounteredException("YOLO", nextResetDate);
        when(workerHandler.handleQueueMessage(eq(configuration), any(QueueMessage.class)))
                .thenThrow(rateLimitEncounteredException);
        when(workerService.getWorkerName()).thenReturn(ANY_WORKER_DESCRIPTOR);
        when(alarmService.createNewAlarm(anyLong(), eq(ANY_QUEUE_JOB_NAME), eq(ANY_WORKER_DESCRIPTOR)))
                .thenReturn(alarm);

        worker.doWork(configuration);

        verify(alarmService).createNewAlarm(anyLong(), eq(ANY_QUEUE_JOB_NAME), eq(ANY_WORKER_DESCRIPTOR));
        verify(alarmService).postAlarm(any(Alarm.class), not(eq(configuration)));
        verifyNoErrorNotified();
    }

    @Test
    public void givenAnyQueueAndErroringWorkerHandler_whenDoWork_thenDoNotPurgeDeleteQueue() throws Throwable {

        Queue<QueueMessage> queue = givenAnyQueueAndErroringWorkerHandler_whenDoWork();

        verify(queue, never()).purgeQueue();
        verify(queueService, never()).removeQueue(queue);
    }

    @Test
    public void givenAnyQueueAndErroringWorkerHandler_whenDoWork_thenNotifyError() throws Throwable {

        givenAnyQueueAndErroringWorkerHandler_whenDoWork();

        verifyErrorNotified(Exception.class);
    }

    @Test
    public void givenAnyQueueAndErroringWorkerHandler_whenDoWork_thenDoNotReschedule() throws Throwable {

        givenAnyQueueAndErroringWorkerHandler_whenDoWork();

        verifyNoRescheduleImmediately();
    }

    private Queue<QueueMessage> givenAnyQueueAndErroringWorkerHandler_whenDoWork() throws
            EmptyQueueException,
            MessageConverterException,
            com.novoda.github.reports.batch.MessageNotSupportedException,
            WorkerOperationFailedException,
            RetriableNetworkException {

        Queue<QueueMessage> queue = givenAnyQueue();
        when(workerHandler.handleQueueMessage(eq(configuration), any(QueueMessage.class))).thenThrow(Exception.class);

        worker.doWork(configuration);
        return queue;
    }

    @Test
    public void givenFailingQueueAddItems_whenDoWork_thenNotifyQueueOperationFailedException() throws
            EmptyQueueException,
            MessageConverterException,
            QueueOperationFailedException,
            WorkerOperationFailedException,
            NotifierOperationFailedException {

        givenFailingQueueAddItems_whenDoWork();

        verifyErrorNotified(QueueOperationFailedException.class);
    }

    @Test
    public void givenFailingQueueAddItems_whenDoWork_thenReschedule() throws
            EmptyQueueException,
            MessageConverterException,
            QueueOperationFailedException,
            WorkerOperationFailedException,
            NotifierOperationFailedException,
            WorkerStartException {

        givenFailingQueueAddItems_whenDoWork();

        verifyRescheduleImmediately();
    }

    private void givenFailingQueueAddItems_whenDoWork() throws
            EmptyQueueException,
            MessageConverterException,
            QueueOperationFailedException,
            WorkerOperationFailedException {

        Queue<QueueMessage> queue = givenAnyQueue();
        when(queue.addItems(anyListOf(QueueMessage.class))).thenThrow(QueueOperationFailedException.class);

        worker.doWork(configuration);
    }

    @Test
    public void givenFailingQueueAddItemsAndCantReschedule_whenDoWork_thenNotifyQueueOperationFailedException() throws
            EmptyQueueException,
            MessageConverterException,
            QueueOperationFailedException,
            WorkerOperationFailedException,
            NotifierOperationFailedException,
            WorkerStartException {

        givenFailingQueueAddItemsAndCantReschedule_whenDoWork();

        verifyErrorNotified(QueueOperationFailedException.class);
    }

    @Test
    public void givenFailingQueueAddItemsAndCantReschedule_whenDoWork_thenNotifyWorkerStartException() throws
            EmptyQueueException,
            MessageConverterException,
            QueueOperationFailedException,
            WorkerOperationFailedException,
            NotifierOperationFailedException,
            WorkerStartException {

        givenFailingQueueAddItemsAndCantReschedule_whenDoWork();

        verifyErrorNotified(WorkerStartException.class);
    }

    private void givenFailingQueueAddItemsAndCantReschedule_whenDoWork() throws
            EmptyQueueException,
            MessageConverterException,
            QueueOperationFailedException,
            WorkerStartException,
            WorkerOperationFailedException {

        Queue<QueueMessage> queue = givenAnyQueue();
        when(queue.addItems(anyListOf(QueueMessage.class))).thenThrow(QueueOperationFailedException.class);
        doThrow(WorkerStartException.class).when(workerService).startWorker(any());

        worker.doWork(configuration);
    }

    @Test
    public void givenFailingNetwork_whenDoWork_thenRescheduleImmediately() throws
            EmptyQueueException,
            MessageConverterException,
            RetriableNetworkException,
            MessageNotSupportedException,
            WorkerOperationFailedException,
            WorkerStartException {

        givenValidQueueAndFailingNetwork();

        worker.doWork(configuration);

        verifyRescheduleImmediately();
    }

    @Test
    public void givenFailingNetwork_whenDoWork_thenDoNotNotifyError() throws
            EmptyQueueException,
            MessageConverterException,
            RetriableNetworkException,
            MessageNotSupportedException,
            WorkerOperationFailedException,
            WorkerStartException,
            NotifierOperationFailedException {

        givenValidQueueAndFailingNetwork();

        worker.doWork(configuration);

        verifyNoErrorNotified();
    }

    private void givenValidQueueAndFailingNetwork() throws
            EmptyQueueException,
            MessageConverterException,
            MessageNotSupportedException,
            RetriableNetworkException {

        givenAnyQueue();
        RetriableNetworkException networkException = new RetriableNetworkException(new SocketTimeoutException());
        when(workerHandler.handleQueueMessage(eq(configuration), any(QueueMessage.class)))
                .thenThrow(networkException);
    }

    @Test
    public void givenAnyQueue_whenDoWork_thenRescheduleImmediately()
            throws EmptyQueueException, MessageConverterException, WorkerOperationFailedException, WorkerStartException {

        givenAnyQueue();

        worker.doWork(configuration);

        verifyRescheduleImmediately();
    }

    private Queue<QueueMessage> givenAnyQueue() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> anyQueue = mock(DefaultQueue.class);
        QueueMessage queueMessage = mock(QueueMessage.class);
        when(anyQueue.getItem()).thenReturn(queueMessage);
        return givenQueue(anyQueue);
    }

    private Queue<QueueMessage> givenEmptyQueue() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> emptyQueue = mock(DefaultQueue.class);
        when(emptyQueue.getItem()).thenThrow(EmptyQueueException.class);
        return givenQueue(emptyQueue);
    }

    private Queue<QueueMessage> givenInvalidMessagesQueue() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> emptyQueue = mock(DefaultQueue.class);
        when(emptyQueue.getItem()).thenThrow(MessageConverterException.class);
        return givenQueue(emptyQueue);
    }

    private Queue<QueueMessage> givenQueue(Queue<QueueMessage> queue) {
        when(queueService.getQueue(ANY_QUEUE_JOB_NAME)).thenReturn(queue);
        return queue;
    }

    private void verifyNoErrorNotified() throws NotifierOperationFailedException {
        verify(notifier, never()).notifyError(any(DefaultConfiguration.class), any(Exception.class));
    }

    private void verifyNoRescheduleImmediately() throws WorkerStartException {
        verify(workerService, never()).startWorker(configuration);
    }

    private <T extends Exception> void verifyErrorNotified(Class<T> exception) throws NotifierOperationFailedException {
        verify(notifier).notifyError(any(DefaultConfiguration.class), isA(exception));
    }

    private void verifyRescheduleImmediately() throws WorkerStartException {
        verify(workerService, only()).startWorker(not(eq(configuration)));
    }

}
