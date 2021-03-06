package com.novoda.github.reports.batch.aws.queue;

import com.amazonaws.services.sqs.model.Message;
import com.novoda.github.reports.batch.queue.MessageConverterException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AmazonQueueMessageConverterTest {

    private static final String ANY_RECEIPT_HANDLE = "banana";
    private static final boolean ANY_TERMINAL = true;
    private static final Long ANY_PAGE = 1337L;
    private static final String ANY_ORGANISATION = "novoda";
    private static final Date ANY_DATE = new GregorianCalendar(2016, 0, 1).getTime();
    private static final Long ANY_REPO_ID = 666L;
    private static final String ANY_REPO_NAME = "yolo";
    private static final Long ANY_ISSUE_NUMBER = 42L;
    private static final Long ANY_ISSUE_OWNER_ID = 123L;
    private static final Boolean ANY_ISSUE_NOT_PR = false;
    private static final Boolean ANY_ISSUE_PR = true;

    private static final AmazonGetRepositoriesQueueMessage AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE = AmazonGetRepositoriesQueueMessage.create(
            ANY_TERMINAL,
            ANY_PAGE,
            ANY_RECEIPT_HANDLE,
            ANY_ORGANISATION,
            ANY_DATE
    );
    private static final AmazonGetIssuesQueueMessage AMAZON_GET_ISSUES_QUEUE_MESSAGE = AmazonGetIssuesQueueMessage.create(
            ANY_TERMINAL,
            ANY_PAGE,
            ANY_RECEIPT_HANDLE,
            ANY_ORGANISATION,
            ANY_DATE,
            ANY_REPO_ID,
            ANY_REPO_NAME
    );
    private static final AmazonGetCommentsQueueMessage AMAZON_GET_COMMENTS_QUEUE_MESSAGE = AmazonGetCommentsQueueMessage.create(
            ANY_TERMINAL,
            ANY_PAGE,
            ANY_RECEIPT_HANDLE,
            ANY_ORGANISATION,
            ANY_DATE,
            ANY_REPO_ID,
            ANY_REPO_NAME,
            ANY_ISSUE_NUMBER,
            ANY_ISSUE_OWNER_ID,
            ANY_ISSUE_NOT_PR
    );
    private static final AmazonGetEventsQueueMessage AMAZON_GET_EVENTS_QUEUE_MESSAGE = AmazonGetEventsQueueMessage.create(
            ANY_TERMINAL,
            ANY_PAGE,
            ANY_RECEIPT_HANDLE,
            ANY_ORGANISATION,
            ANY_DATE,
            ANY_REPO_ID,
            ANY_REPO_NAME,
            ANY_ISSUE_NUMBER,
            ANY_ISSUE_OWNER_ID,
            ANY_ISSUE_NOT_PR
    );
    private static final AmazonGetReviewCommentsQueueMessage AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE = AmazonGetReviewCommentsQueueMessage.create(
            ANY_TERMINAL,
            ANY_PAGE,
            ANY_RECEIPT_HANDLE,
            ANY_ORGANISATION,
            ANY_DATE,
            ANY_REPO_ID,
            ANY_REPO_NAME,
            ANY_ISSUE_NUMBER,
            ANY_ISSUE_OWNER_ID,
            ANY_ISSUE_PR
    );
    private static final AmazonGetReactionsQueueMessage AMAZON_GET_REACTIONS_QUEUE_MESSAGE = AmazonGetReactionsQueueMessage.create(
            ANY_TERMINAL,
            ANY_PAGE,
            ANY_RECEIPT_HANDLE,
            ANY_ORGANISATION,
            ANY_DATE,
            ANY_REPO_ID,
            ANY_REPO_NAME,
            ANY_ISSUE_NUMBER,
            ANY_ISSUE_OWNER_ID,
            ANY_ISSUE_PR
    );

    private AmazonQueueMessageConverter converter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        converter = AmazonQueueMessageConverter.newInstance();
    }

    @Test
    public void givenRepositoriesMessage_whenFromMessage_thenReturnGetRepositoriesMessage() throws IOException, MessageConverterException {
        Message message = givenMessageFromJson("repositories.json");

        AmazonQueueMessage expected = converter.fromMessage(message);

        assertEquals(AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE, expected);
    }

    @Test
    public void givenIssuesMessage_whenFromMessage_thenReturnGetIssuesMessage() throws IOException, MessageConverterException {
        Message message = givenMessageFromJson("issues.json");

        AmazonQueueMessage expected = converter.fromMessage(message);

        assertEquals(AMAZON_GET_ISSUES_QUEUE_MESSAGE, expected);
    }

    @Test
    public void givenCommentsMessage_whenFromMessage_thenReturnGetCommentsMessage() throws IOException, MessageConverterException {
        Message message = givenMessageFromJson("comments.json");

        AmazonQueueMessage expected = converter.fromMessage(message);

        assertEquals(AMAZON_GET_COMMENTS_QUEUE_MESSAGE, expected);
    }

    @Test
    public void givenEventsMessage_whenFromMessage_thenReturnGetEventsMessage() throws IOException, MessageConverterException {
        Message message = givenMessageFromJson("events.json");

        AmazonQueueMessage expected = converter.fromMessage(message);

        assertEquals(AMAZON_GET_EVENTS_QUEUE_MESSAGE, expected);
    }

    @Test
    public void givenReviewCommentsMessage_whenFromMessage_thenReturnGetReviewCommentsMessage() throws IOException, MessageConverterException {
        Message message = givenMessageFromJson("review_comments.json");

        AmazonQueueMessage expected = converter.fromMessage(message);

        assertEquals(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE, expected);
    }

    @Test
    public void givenReactionsMessage_whenFromMessage_thenReturnGetReactionsMessage() throws IOException, MessageConverterException {
        Message message = givenMessageFromJson("reactions.json");

        AmazonQueueMessage expected = converter.fromMessage(message);

        assertEquals(AMAZON_GET_REACTIONS_QUEUE_MESSAGE, expected);
    }

    @Test
    public void givenEmptyMessageBody_whenFromMessage_thenThrowMessageConverterException() throws MessageConverterException {
        Message message = new Message().withBody("");

        expectedException.expect(MessageConverterException.class);
        converter.fromMessage(message);
    }

    @Test
    public void givenInvalidMessageType_whenFromMessage_thenThrowMessageConverterException() throws MessageConverterException {
        Message message = new Message().withBody("{\"type\": \"lol\"}");

        expectedException.expect(MessageConverterException.class);
        converter.fromMessage(message);
    }

    private Message givenMessageFromJson(String resource) throws IOException {
        String json = givenJsonFromResource(resource);
        return new Message().withBody(json).withReceiptHandle(ANY_RECEIPT_HANDLE);
    }

    @Test
    public void givenGetRepositoriesMessage_whenToMessage_thenReturnRepositoriesMessage() throws IOException {

        AmazonRawQueueMessage actual = converter.toRawMessage(AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE);

        AmazonRawQueueMessage expected = AmazonRawQueueMessage.builder()
                .type(AmazonRawQueueMessage.Type.REPOSITORIES)
                .organisationName(AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE.organisationName())
                .since(AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE.sinceOrNull())
                .isTerminal(AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE.localTerminal())
                .page(AMAZON_GET_REPOSITORIES_QUEUE_MESSAGE.page())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void givenGetIssuesMessage_whenToMessage_thenReturnIssuesMessage() throws IOException {

        AmazonRawQueueMessage actual = converter.toRawMessage(AMAZON_GET_ISSUES_QUEUE_MESSAGE);

        AmazonRawQueueMessage expected = AmazonRawQueueMessage.builder()
                .type(AmazonRawQueueMessage.Type.ISSUES)
                .organisationName(AMAZON_GET_ISSUES_QUEUE_MESSAGE.organisationName())
                .since(AMAZON_GET_ISSUES_QUEUE_MESSAGE.sinceOrNull())
                .isTerminal(AMAZON_GET_ISSUES_QUEUE_MESSAGE.localTerminal())
                .page(AMAZON_GET_ISSUES_QUEUE_MESSAGE.page())
                .repositoryName(AMAZON_GET_ISSUES_QUEUE_MESSAGE.repositoryName())
                .repositoryId(AMAZON_GET_ISSUES_QUEUE_MESSAGE.repositoryId())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void givenGetCommentsMessage_whenToMessage_thenReturnCommentsMessage() throws IOException {

        AmazonRawQueueMessage actual = converter.toRawMessage(AMAZON_GET_COMMENTS_QUEUE_MESSAGE);

        AmazonRawQueueMessage expected = AmazonRawQueueMessage.builder()
                .type(AmazonRawQueueMessage.Type.COMMENTS)
                .organisationName(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.organisationName())
                .since(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.sinceOrNull())
                .isTerminal(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.localTerminal())
                .page(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.page())
                .repositoryName(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.repositoryName())
                .repositoryId(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.repositoryId())
                .issueNumber(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.issueNumber())
                .issueOwnerId(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.issueOwnerId())
                .isPullRequest(AMAZON_GET_COMMENTS_QUEUE_MESSAGE.isPullRequest())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void givenGetEventsMessage_whenToMessage_thenReturnEventsMessage() throws IOException {

        AmazonRawQueueMessage actual = converter.toRawMessage(AMAZON_GET_EVENTS_QUEUE_MESSAGE);

        AmazonRawQueueMessage expected = AmazonRawQueueMessage.builder()
                .type(AmazonRawQueueMessage.Type.EVENTS)
                .organisationName(AMAZON_GET_EVENTS_QUEUE_MESSAGE.organisationName())
                .since(AMAZON_GET_EVENTS_QUEUE_MESSAGE.sinceOrNull())
                .isTerminal(AMAZON_GET_EVENTS_QUEUE_MESSAGE.localTerminal())
                .page(AMAZON_GET_EVENTS_QUEUE_MESSAGE.page())
                .repositoryName(AMAZON_GET_EVENTS_QUEUE_MESSAGE.repositoryName())
                .repositoryId(AMAZON_GET_EVENTS_QUEUE_MESSAGE.repositoryId())
                .issueNumber(AMAZON_GET_EVENTS_QUEUE_MESSAGE.issueNumber())
                .issueOwnerId(AMAZON_GET_EVENTS_QUEUE_MESSAGE.issueOwnerId())
                .isPullRequest(AMAZON_GET_EVENTS_QUEUE_MESSAGE.isPullRequest())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void givenGetReviewCommentsMessage_whenToMessage_thenReturnReviewCommentsMessage() throws IOException {

        AmazonRawQueueMessage actual = converter.toRawMessage(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE);

        AmazonRawQueueMessage expected = AmazonRawQueueMessage.builder()
                .type(AmazonRawQueueMessage.Type.REVIEW_COMMENTS)
                .organisationName(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.organisationName())
                .since(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.sinceOrNull())
                .isTerminal(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.localTerminal())
                .page(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.page())
                .repositoryName(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.repositoryName())
                .repositoryId(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.repositoryId())
                .issueNumber(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.issueNumber())
                .issueOwnerId(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.issueOwnerId())
                .isPullRequest(AMAZON_GET_REVIEW_COMMENTS_QUEUE_MESSAGE.isPullRequest())
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void givenGetReactionsMessage_whenToMessage_thenReturnReactionsMessage() throws IOException {

        AmazonRawQueueMessage actual = converter.toRawMessage(AMAZON_GET_REACTIONS_QUEUE_MESSAGE);

        AmazonRawQueueMessage expected = AmazonRawQueueMessage.builder()
                .type(AmazonRawQueueMessage.Type.REACTIONS)
                .organisationName(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.organisationName())
                .since(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.sinceOrNull())
                .isTerminal(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.localTerminal())
                .page(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.page())
                .repositoryName(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.repositoryName())
                .repositoryId(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.repositoryId())
                .issueNumber(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.issueNumber())
                .issueOwnerId(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.issueOwnerId())
                .isPullRequest(AMAZON_GET_REACTIONS_QUEUE_MESSAGE.isPullRequest())
                .build();
        assertEquals(expected, actual);
    }

    private String givenJsonFromResource(String resource) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        return buffer.lines().collect(Collectors.joining("\n"));
    }

}
