package com.novoda.github.reports.aws.queue;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import static com.novoda.github.reports.aws.queue.AmazonRawQueueMessage.Type.*;

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
                rawQueueMessage.issueNumber()
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
                rawQueueMessage.issueNumber()
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
                rawQueueMessage.issueNumber()
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
        AmazonRawQueueMessage rawQueueMessage = null;

        if (message instanceof AmazonGetRepositoriesQueueMessage) {
            rawQueueMessage = toRepositoriesRawMessage((AmazonGetRepositoriesQueueMessage) message);
        } else if (message instanceof AmazonGetIssuesQueueMessage) {
            rawQueueMessage = toGetIssuesRawMessage((AmazonGetIssuesQueueMessage) message);
        } else if (message instanceof AmazonGetCommentsQueueMessage) {
            rawQueueMessage = toGetCommentsRawMessage((AmazonGetCommentsQueueMessage) message);
        } else if (message instanceof AmazonGetEventsQueueMessage) {
            rawQueueMessage = toGetEventsRawMessage((AmazonGetEventsQueueMessage) message);
        } else if (message instanceof AmazonGetReviewCommentsQueueMessage) {
            rawQueueMessage = toGetReviewCommentsRawMessage((AmazonGetReviewCommentsQueueMessage) message);
        }

        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toGetCommentsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetGenericEventsRawMessage(message);
        return rawQueueMessage.withType(COMMENTS);
    }

    private AmazonRawQueueMessage toGetEventsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetGenericEventsRawMessage(message);
        return rawQueueMessage.withType(EVENTS);
    }

    private AmazonRawQueueMessage toGetReviewCommentsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetGenericEventsRawMessage(message);
        return rawQueueMessage.withType(REVIEW_COMMENTS);
    }

    private AmazonRawQueueMessage toGetGenericEventsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetIssuesRawMessage(message);
        return rawQueueMessage.withIssueNumber(message.issueNumber());
    }

    private AmazonRawQueueMessage toGetIssuesRawMessage(GetIssuesQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toRepositoriesRawMessage(message);
        return rawQueueMessage.withTypeAndRepository(
                ISSUES,
                message.repositoryName(),
                message.repositoryId()
        );
    }

    private AmazonRawQueueMessage toRepositoriesRawMessage(GetRepositoriesQueueMessage message) {
        return AmazonRawQueueMessage.create(
                REPOSITORIES,
                message.organisationName(),
                message.sinceOrNull(),
                message.localTerminal(),
                message.page(),
                null,
                null,
                null
        );
    }
}
