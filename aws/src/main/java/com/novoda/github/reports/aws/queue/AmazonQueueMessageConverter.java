package com.novoda.github.reports.aws.queue;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AmazonQueueMessageConverter {

    private final Gson gson;

    public static AmazonQueueMessageConverter newInstance() {
        Gson gson = new GsonBuilder().create();
        return new AmazonQueueMessageConverter(gson);
    }

    private AmazonQueueMessageConverter(Gson gson) {
        this.gson = gson;
    }

    public AmazonQueueMessage fromMessage(Message message) throws MessageConverterException {
        AmazonRawQueueMessage rawQueueMessage = fromMessageToRaw(message);
        switch (rawQueueMessage.getType()) {
            case REPOSITORIES:
                return toGetRepositoriesMessage(rawQueueMessage, message);
            case ISSUES:
                return toGetIssuesMessage(rawQueueMessage, message);
            case COMMENTS:
                return toGetCommentsMessage(rawQueueMessage, message);
            case EVENTS:
                return toGetEventsMessage(rawQueueMessage, message);
            case REVIEW_COMMENTS:
                return toGetReviewCommentsMessage(rawQueueMessage, message);
            default:
                throw new MessageConverterException("Can't convert type " + rawQueueMessage.getType() + ".");
        }
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

    public Message toMessage(AmazonQueueMessage message) {
        AmazonRawQueueMessage rawQueueMessage = new AmazonRawQueueMessage();

        rawQueueMessage.isTerminal = message.localTerminal();
        rawQueueMessage.page = message.page();

        if (message instanceof AmazonGetRepositoriesQueueMessage) {
            rawQueueMessage.organisationName = ((AmazonGetRepositoriesQueueMessage) message).organisationName();
            rawQueueMessage.since = ((AmazonGetRepositoriesQueueMessage) message).since();
        }
        if (message instanceof AmazonGetIssuesQueueMessage) {
            rawQueueMessage.repositoryId = ((AmazonGetIssuesQueueMessage) message).repositoryId();
            rawQueueMessage.repositoryName = ((AmazonGetIssuesQueueMessage) message).repositoryName();
        }
        if (message instanceof AmazonGetCommentsQueueMessage) {
            rawQueueMessage.issueNumber = ((AmazonGetCommentsQueueMessage) message).issueNumber();
        }
        if (message instanceof AmazonGetEventsQueueMessage) {
            rawQueueMessage.issueNumber = ((AmazonGetEventsQueueMessage) message).issueNumber();
        }
        if (message instanceof AmazonGetReviewCommentsQueueMessage) {
            rawQueueMessage.issueNumber = ((AmazonGetReviewCommentsQueueMessage) message).issueNumber();
        }

        String messageBody = gson.toJson(rawQueueMessage);
        return new Message()
                .withReceiptHandle(message.receiptHandle())
                .withBody(messageBody);
    }
}
