package com.novoda.github.reports.batch.aws.notifier;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.logger.Logger;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierOperationFailedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

class EmailNotifier implements Notifier<EmailNotifierConfiguration, AmazonConfiguration> {

    private static final String COMPLETION_SUBJECT = "[github-reports] Your job has completed!";
    private static final String COMPLETION_BODY = "The job with name \"%s\" has completed successfully.";
    private static final String ERROR_SUBJECT = "[github-reports] Your job has failed!";
    private static final String ERROR_BODY = "The job with name \"%s\" has failed because of an error.";
    private static final AdditionalInfo NO_ADDITIONAL_INFO = null;

    private final Email email;
    private final Logger logger;
    private final AdditionalInfo additionalInfo;

    public static EmailNotifier newInstance(Logger logger, AdditionalInfo additionalInfo) {
        return new EmailNotifier(new SimpleEmail(), logger, additionalInfo);
    }

    public static EmailNotifier newInstance(Logger logger) {
        return new EmailNotifier(new SimpleEmail(), logger, NO_ADDITIONAL_INFO);
    }

    EmailNotifier(Email email, Logger logger, AdditionalInfo additionalInfo) {
        this.email = email;
        this.logger = logger;
        this.additionalInfo = additionalInfo;
    }

    @Override
    public void notifyCompletion(AmazonConfiguration configuration) throws NotifierOperationFailedException {
        logger.debug("Notifying completion...");
        sendEmail(configuration, COMPLETION_SUBJECT, COMPLETION_BODY);
        logger.debug("Completion notified.");
    }

    @Override
    public void notifyError(AmazonConfiguration configuration, Throwable throwable) throws NotifierOperationFailedException {
        logger.warn("Notifying error: %s", throwable);
        sendEmail(configuration, ERROR_SUBJECT, getErrorBody(throwable));
        logger.warn("Error notified.");
    }

    private String getErrorBody(Throwable throwable) {
        StringWriter exceptionWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(exceptionWriter);
        throwable.printStackTrace(printWriter);

        return ERROR_BODY + "\n\n" + exceptionWriter.toString();
    }

    private void sendEmail(AmazonConfiguration configuration, String subjectTemplate, String bodyTemplate) throws NotifierOperationFailedException {
        try {
            Email email = buildEmail(configuration, subjectTemplate, bodyTemplate);
            email.send();
        } catch (EmailException e) {
            throw new NotifierOperationFailedException(e);
        }
    }

    private Email buildEmail(AmazonConfiguration configuration, String subjectTemplate, String bodyTemplate)
            throws EmailException, NotifierOperationFailedException {

        EmailNotifierConfiguration emailNotifierConfiguration = configuration.notifierConfiguration();

        email.setHostName(emailNotifierConfiguration.host());
        email.setSmtpPort(emailNotifierConfiguration.port());
        email.setAuthenticator(new DefaultAuthenticator(emailNotifierConfiguration.username(), emailNotifierConfiguration.password()));
        email.setSSLOnConnect(emailNotifierConfiguration.useSsl());
        email.setFrom(emailNotifierConfiguration.from());

        String subject = String.format(subjectTemplate, configuration.jobName());
        email.setSubject(subject);

        String body = buildBody(configuration, bodyTemplate);
        email.setMsg(body);

        addRecipients(email, emailNotifierConfiguration.to());
        return email;
    }

    private String buildBody(AmazonConfiguration configuration, String bodyTemplate) {
        String body = String.format(bodyTemplate, configuration.jobName());
        if (additionalInfo != null) {
            body += "\nAdditional information:\n";
            body += additionalInfo.describeAdditionalInfo();
        }
        return body;
    }

    private void addRecipients(Email email, List<String> recipients) throws NotifierOperationFailedException {
        for (String recipient : recipients) {
            try {
                email.addTo(recipient);
            } catch (EmailException e) {
                throw new NotifierOperationFailedException(e);
            }
        }
    }

}
