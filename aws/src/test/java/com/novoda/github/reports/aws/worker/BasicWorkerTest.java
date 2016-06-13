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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BasicWorkerTest {

    private static final String ANY_QUEUE_NAME = "some_queue";

    @Mock
    private WorkerService workerService;

    @Mock
    private AlarmService alarmService;

    @Mock
    private QueueService<Queue<QueueMessage>> queueService;

    @Mock
    private NotifierService notifierService;

    @Mock
    private WorkerHandlerService workerHandlerService;

    @InjectMocks
    private BasicWorker worker;

    @Mock
    private EventSource eventSource;

    @Mock
    private Notifier notifier;

    @Mock
    private WorkerHandler<QueueMessage> workerHandler;

    @Before
    public void setUp() {
        initMocks(this);

        when(notifierService.getNotifier()).thenReturn(notifier);
        when(workerHandlerService.getWorkerHandler()).thenReturn(workerHandler);
    }

    @Test
    public void givenStartedFromAlarm_whenDoWork_thenRemoveAlarm() throws EmptyQueueException, MessageConverterException {
        eventSource = mock(Alarm.class);
        givenAnyQueue();

        worker.doWork(eventSource);

        verify(alarmService).removeAlarm((Alarm) eventSource);
    }

    @Test
    public void givenStartedFromNonAlarm_whenDoWork_thenDoNotRemoveAlarm() throws EmptyQueueException, MessageConverterException {
        givenAnyQueue();

        worker.doWork(eventSource);

        verify(alarmService, never()).removeAlarm(any(Alarm.class));
    }

    @Test
    public void givenEmptyQueue_whenDoWork_thenNotifyCompletionAndDeleteQueueAndDoNotReschedule() throws EmptyQueueException, MessageConverterException {
        Queue<QueueMessage> queue = givenEmptyQueue();

        worker.doWork(eventSource);

        verify(notifier).notifyCompletion(eventSource.getConfiguration().getNotifierConfiguration());
        verify(queueService).removeQueue(queue);
        verifyNoErrorNotified();
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenIncompatibleMessageInQueue_whenDoWork_thenNotifyConversionErrorAndDoNotReschedule()
            throws EmptyQueueException, MessageConverterException {

        givenInvalidMessagesQueue();

        worker.doWork(eventSource);

        verifyErrorNotified(MessageConverterException.class);
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenValidQueue_whenDoWork_thenDelegateToHandleQueueMessage() throws Exception, RateLimitEncounteredException {
        givenAnyQueue();

        worker.doWork(eventSource);

        verify(workerHandler).handleQueueMessage(any(Configuration.class), any(QueueMessage.class));
    }

    @Test
    public void givenValidQueueAndOutOfGithubToken_whenDoWork_thenRescheduleForLater() throws Exception, RateLimitEncounteredException {
        givenAnyQueue();
        Instant nextResetInstant = Instant.now().plusSeconds(600);
        Date nextResetDate = new Date(nextResetInstant.toEpochMilli());
        RateLimitEncounteredException rateLimitEncounteredException = new RateLimitEncounteredException("YOLO", nextResetDate);
        when(workerHandler.handleQueueMessage(any(Configuration.class), any(QueueMessage.class))).thenThrow(rateLimitEncounteredException);

        worker.doWork(eventSource);

        verify(alarmService).createAlarm(any(Configuration.class), anyLong());
        verify(alarmService).postAlarm(any(Alarm.class));
        verifyNoErrorNotified();
    }

    @Test
    public void givenAnyQueueAndErroringWorkerHandler_whenDoWork_thenPurgeDeleteQueueAndNotifyErrorAndDoNotReschedule() throws Exception, RateLimitEncounteredException {
        Queue<QueueMessage> queue = givenAnyQueue();
        when(workerHandler.handleQueueMessage(any(Configuration.class), any(QueueMessage.class))).thenThrow(Exception.class);

        worker.doWork(eventSource);

        verify(queue).purgeQueue();
        verify(queueService).removeQueue(queue);
        verifyErrorNotified(Exception.class);
        verifyNoRescheduleImmediately();
    }

    @Test
    public void givenFailingQueueAddItems_whenDoWork_thenNotifyErrorAndReschedule()
            throws EmptyQueueException, MessageConverterException, QueueOperationFailedException {

        Queue<QueueMessage> queue = givenAnyQueue();
        when(queue.addItems(anyListOf(QueueMessage.class))).thenThrow(QueueOperationFailedException.class);

        worker.doWork(eventSource);

        verifyErrorNotified(QueueOperationFailedException.class);
    }

    @Test
    public void givenAnyQueue_whenDoWork_thenRescheduleImmediately() throws EmptyQueueException, MessageConverterException {
        givenAnyQueue();

        worker.doWork(eventSource);

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
        Configuration mockConfig = mock(Configuration.class);
        when(mockConfig.getQueueName()).thenReturn(ANY_QUEUE_NAME);
        when(eventSource.getConfiguration()).thenReturn(mockConfig);
        when(queueService.getQueue(ANY_QUEUE_NAME)).thenReturn(queue);
        return queue;
    }

    private void verifyNoErrorNotified() {
        verify(notifier, never()).notifyError(any(NotifierConfiguration.class), any(Exception.class));
    }

    private void verifyNoRescheduleImmediately() {
        verify(workerService, never()).startWorker(eventSource.getConfiguration());
    }

    private <T extends Exception> void verifyErrorNotified(Class<T> exception) {
        verify(notifier).notifyError(any(NotifierConfiguration.class), any(exception));
    }

    private void verifyRescheduleImmediately() {
        verify(workerService).startWorker(eventSource.getConfiguration());
    }

}
