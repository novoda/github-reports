package com.novoda.github.reports.batch.aws.queue;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.batch.queue.GetCommentsQueueMessage;
import com.novoda.github.reports.batch.queue.GetIssuesQueueMessage;
import com.novoda.github.reports.batch.queue.GetRepositoriesQueueMessage;
import com.novoda.github.reports.batch.queue.MessageConverterException;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import static com.novoda.github.reports.batch.aws.queue.AmazonRawQueueMessage.Type.*;

class AmazonQueueMessageConverter {

    private static final String ISO_8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final Gson gson;

    static AmazonQueueMessageConverter newInstance() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .setDateFormat(ISO_8601_DATE_TIME_FORMAT)
                .create();
        return new AmazonQueueMessageConverter(gson);
    }

    private AmazonQueueMessageConverter(Gson gson) {
        this.gson = gson;
    }

    AmazonQueueMessage fromMessage(Message message) throws MessageConverterException {
        AmazonRawQueueMessage rawQueueMessage = fromMessageToRaw(message);

        if (rawQueueMessage != null) {
            AmazonRawQueueMessage.Type messageType = rawQueueMessage.type();
            if (messageType == REPOSITORIES) {
                return toGetRepositoriesMessage(rawQueueMessage, message);
            }
            if (messageType == ISSUES) {
                return toGetIssuesMessage(rawQueueMessage, message);
            }
            if (messageType == COMMENTS) {
                return toGetCommentsMessage(rawQueueMessage, message);
            }
            if (messageType == EVENTS) {
                return toGetEventsMessage(rawQueueMessage, message);
            }
            if (messageType == REVIEW_COMMENTS) {
                return toGetReviewCommentsMessage(rawQueueMessage, message);
            }
            throw new MessageConverterException("Can't convert type " + rawQueueMessage.type() + ".");
        }

        throw new MessageConverterException("Can't convert an empty or invalid JSON.");
    }

    private AmazonRawQueueMessage fromMessageToRaw(Message message) throws MessageConverterException {
        try {
            String json = message.getBody();
            return gson.fromJson(json, AmazonRawQueueMessage.class);
        } catch (Exception e) {
            throw new MessageConverterException(e);
        }
    }

    private AmazonGetRepositoriesQueueMessage toGetRepositoriesMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetRepositoriesQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.page(),
                message.getReceiptHandle(),
                rawQueueMessage.organisationName(),
                rawQueueMessage.since()
        );
    }

    private AmazonQueueMessage toGetIssuesMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetIssuesQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.page(),
                message.getReceiptHandle(),
                rawQueueMessage.organisationName(),
                rawQueueMessage.since(),
                rawQueueMessage.repositoryId(),
                rawQueueMessage.repositoryName()
        );
    }

    private AmazonQueueMessage toGetCommentsMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetCommentsQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.page(),
                message.getReceiptHandle(),
                rawQueueMessage.organisationName(),
                rawQueueMessage.since(),
                rawQueueMessage.repositoryId(),
                rawQueueMessage.repositoryName(),
                rawQueueMessage.issueNumber(),
                rawQueueMessage.issueOwnerId()
        );
    }

    private AmazonQueueMessage toGetEventsMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetEventsQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.page(),
                message.getReceiptHandle(),
                rawQueueMessage.organisationName(),
                rawQueueMessage.since(),
                rawQueueMessage.repositoryId(),
                rawQueueMessage.repositoryName(),
                rawQueueMessage.issueNumber(),
                rawQueueMessage.issueOwnerId()
        );
    }

    private AmazonQueueMessage toGetReviewCommentsMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetReviewCommentsQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.page(),
                message.getReceiptHandle(),
                rawQueueMessage.organisationName(),
                rawQueueMessage.since(),
                rawQueueMessage.repositoryId(),
                rawQueueMessage.repositoryName(),
                rawQueueMessage.issueNumber(),
                rawQueueMessage.issueOwnerId()
        );
    }

    Message toMessage(AmazonQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toRawMessage(message);

        String messageBody = gson.toJson(rawQueueMessage);
        return new Message()
                .withReceiptHandle(message.receiptHandle())
                .withBody(messageBody);
    }

    AmazonRawQueueMessage toRawMessage(AmazonQueueMessage message) {
        AmazonRawQueueMessage.Builder rawQueueMessageBuilder = AmazonRawQueueMessage.builder();

        if (message instanceof AmazonGetRepositoriesQueueMessage) {
            toRepositoriesRawMessage((AmazonGetRepositoriesQueueMessage) message, rawQueueMessageBuilder);
        } else if (message instanceof AmazonGetIssuesQueueMessage) {
            toGetIssuesRawMessage((AmazonGetIssuesQueueMessage) message, rawQueueMessageBuilder);
        } else if (message instanceof AmazonGetCommentsQueueMessage) {
            toGetCommentsRawMessage((AmazonGetCommentsQueueMessage) message, rawQueueMessageBuilder);
        } else if (message instanceof AmazonGetEventsQueueMessage) {
            toGetEventsRawMessage((AmazonGetEventsQueueMessage) message, rawQueueMessageBuilder);
        } else if (message instanceof AmazonGetReviewCommentsQueueMessage) {
            toGetReviewCommentsRawMessage((AmazonGetReviewCommentsQueueMessage) message, rawQueueMessageBuilder);
        }

        return rawQueueMessageBuilder.build();
    }

    private void toGetCommentsRawMessage(GetCommentsQueueMessage message,
                                         AmazonRawQueueMessage.Builder rawQueueMessageBuilder) {

        toGetGenericEventsRawMessage(message, rawQueueMessageBuilder);
        rawQueueMessageBuilder.type(COMMENTS);
    }

    private void toGetEventsRawMessage(GetCommentsQueueMessage message,
                                       AmazonRawQueueMessage.Builder rawQueueMessageBuilder) {
        toGetGenericEventsRawMessage(message, rawQueueMessageBuilder);
        rawQueueMessageBuilder.type(EVENTS);
    }

    private void toGetReviewCommentsRawMessage(GetCommentsQueueMessage message,
                                               AmazonRawQueueMessage.Builder rawQueueMessageBuilder) {
        toGetGenericEventsRawMessage(message, rawQueueMessageBuilder);
        rawQueueMessageBuilder.type(REVIEW_COMMENTS);
    }

    private void toGetGenericEventsRawMessage(GetCommentsQueueMessage message,
                                              AmazonRawQueueMessage.Builder rawQueueMessageBuilder) {
        toGetIssuesRawMessage(message, rawQueueMessageBuilder);
        rawQueueMessageBuilder.issueNumber(message.issueNumber());
        rawQueueMessageBuilder.issueOwnerId(message.issueOwnerId());
    }

    private void toGetIssuesRawMessage(GetIssuesQueueMessage message,
                                       AmazonRawQueueMessage.Builder rawQueueMessageBuilder) {
        toRepositoriesRawMessage(message, rawQueueMessageBuilder);
        rawQueueMessageBuilder
                .type(ISSUES)
                .repositoryName(message.repositoryName())
                .repositoryId(message.repositoryId());
    }

    private void toRepositoriesRawMessage(GetRepositoriesQueueMessage message,
                                          AmazonRawQueueMessage.Builder rawQueueMessageBuilder) {
        rawQueueMessageBuilder
                .type(REPOSITORIES)
                .organisationName(message.organisationName())
                .since(message.sinceOrNull())
                .isTerminal(message.localTerminal())
                .page(message.page());
    }
}
