package com.novoda.github.reports.aws.queue;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.novoda.github.reports.aws.queue.AmazonRawQueueMessage.Type.*;

class AmazonQueueMessageConverter {

    private static final String ISO_8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final Gson gson;

    static AmazonQueueMessageConverter newInstance() {
        Gson gson = new GsonBuilder().setDateFormat(ISO_8601_DATE_TIME_FORMAT).create();
        return new AmazonQueueMessageConverter(gson);
    }

    private AmazonQueueMessageConverter(Gson gson) {
        this.gson = gson;
    }

    AmazonQueueMessage fromMessage(Message message) throws MessageConverterException {
        AmazonRawQueueMessage rawQueueMessage = fromMessageToRaw(message);

        if (rawQueueMessage != null) {
            AmazonRawQueueMessage.Type messageType = rawQueueMessage.getType();
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
            throw new MessageConverterException("Can't convert type " + rawQueueMessage.getType() + ".");
        }

        throw new MessageConverterException("Can't convert an empty or invalid JSON.");
    }

    private AmazonRawQueueMessage fromMessageToRaw(Message message) {
        String json = message.getBody();
        return gson.fromJson(json, AmazonRawQueueMessage.class);
    }

    private AmazonGetRepositoriesQueueMessage toGetRepositoriesMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetRepositoriesQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.getPage(),
                message.getReceiptHandle(),
                rawQueueMessage.getOrganisationName(),
                rawQueueMessage.getSince()
        );
    }

    private AmazonQueueMessage toGetIssuesMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetIssuesQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.getPage(),
                message.getReceiptHandle(),
                rawQueueMessage.getOrganisationName(),
                rawQueueMessage.getSince(),
                rawQueueMessage.getRepositoryId(),
                rawQueueMessage.getRepositoryName()
        );
    }

    private AmazonQueueMessage toGetCommentsMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetCommentsQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.getPage(),
                message.getReceiptHandle(),
                rawQueueMessage.getOrganisationName(),
                rawQueueMessage.getSince(),
                rawQueueMessage.getRepositoryId(),
                rawQueueMessage.getRepositoryName(),
                rawQueueMessage.getIssueNumber()
        );
    }

    private AmazonQueueMessage toGetEventsMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetEventsQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.getPage(),
                message.getReceiptHandle(),
                rawQueueMessage.getOrganisationName(),
                rawQueueMessage.getSince(),
                rawQueueMessage.getRepositoryId(),
                rawQueueMessage.getRepositoryName(),
                rawQueueMessage.getIssueNumber()
        );
    }

    private AmazonQueueMessage toGetReviewCommentsMessage(AmazonRawQueueMessage rawQueueMessage, Message message) {
        return AmazonGetReviewCommentsQueueMessage.create(
                rawQueueMessage.isTerminal(),
                rawQueueMessage.getPage(),
                message.getReceiptHandle(),
                rawQueueMessage.getOrganisationName(),
                rawQueueMessage.getSince(),
                rawQueueMessage.getRepositoryId(),
                rawQueueMessage.getRepositoryName(),
                rawQueueMessage.getIssueNumber()
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
        AmazonRawQueueMessage rawQueueMessage = new AmazonRawQueueMessage();

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

        rawQueueMessage.isTerminal = message.localTerminal();
        rawQueueMessage.page = message.page();

        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toGetCommentsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetGenericEventsRawMessage(message);
        rawQueueMessage.type = COMMENTS;
        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toGetEventsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetGenericEventsRawMessage(message);
        rawQueueMessage.type = EVENTS;
        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toGetReviewCommentsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetGenericEventsRawMessage(message);
        rawQueueMessage.type = REVIEW_COMMENTS;
        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toGetGenericEventsRawMessage(GetCommentsQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toGetIssuesRawMessage(message);
        rawQueueMessage.issueNumber = message.issueNumber();
        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toGetIssuesRawMessage(GetIssuesQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = toRepositoriesRawMessage(message);
        rawQueueMessage.type = ISSUES;
        rawQueueMessage.repositoryId = message.repositoryId();
        rawQueueMessage.repositoryName = message.repositoryName();
        return rawQueueMessage;
    }

    private AmazonRawQueueMessage toRepositoriesRawMessage(GetRepositoriesQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = new AmazonRawQueueMessage();
        rawQueueMessage.type = REPOSITORIES;
        rawQueueMessage.organisationName = message.organisationName();
        rawQueueMessage.since = message.since();
        return rawQueueMessage;
    }
}
