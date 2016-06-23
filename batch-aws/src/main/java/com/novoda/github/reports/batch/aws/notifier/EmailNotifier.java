package com.novoda.github.reports.batch.aws.notifier;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.notifier.Notifier;
import com.novoda.github.reports.batch.notifier.NotifierOperationFailedException;
import com.novoda.github.reports.batch.logger.Logger;

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
    private static final String ERROR_SUBJECT = "[github-reports] Your job has errored!";
    private static final String ERROR_BODY = "The job with name \"%s\" has errored.";

    private final Email email;
    private final Logger logger;

    public static EmailNotifier newInstance(Logger logger) {
        return new EmailNotifier(new SimpleEmail(), logger);
    }

    private EmailNotifier(Email email, Logger logger) {
        this.email = email;
        this.logger = logger;
    }

    @Override
    public void notifyCompletion(AmazonConfiguration configuration) throws NotifierOperationFailedException {
        logger.info("Notifying completion...");
        sendEmail(configuration, COMPLETION_SUBJECT, COMPLETION_BODY);
        logger.info("Completion notified.");
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

        String body = String.format(bodyTemplate, configuration.jobName());
        email.setMsg(body);

        addRecipients(email, emailNotifierConfiguration.to());
        return email;
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
