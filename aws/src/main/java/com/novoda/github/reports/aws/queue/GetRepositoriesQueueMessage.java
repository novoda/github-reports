package com.novoda.github.reports.aws.queue;

import java.util.Date;

public interface GetRepositoriesQueueMessage extends QueueMessage {

    String getOrganisation();

    Date getSince();

}
