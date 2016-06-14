package com.novoda.github.reports.aws.notifier;

import org.apache.commons.mail.EmailException;

public class NotifierOperationFailedException extends Exception {

    NotifierOperationFailedException(EmailException e) {
        super(e);
    }

}
