package com.novoda.github.reports.aws.notifier;

import com.novoda.github.reports.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.aws.configuration.EmailNotifierConfiguration;

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

    public static EmailNotifier newInstance() {
        return new EmailNotifier(new SimpleEmail());
    }

    private EmailNotifier(Email email) {
        this.email = email;
    }

    @Override
    public void notifyCompletion(AmazonConfiguration configuration) throws NotifierOperationFailedException {
        sendEmail(configuration, COMPLETION_SUBJECT, COMPLETION_BODY);
    }

    @Override
    public void notifyError(AmazonConfiguration configuration, Throwable throwable) throws NotifierOperationFailedException {
        sendEmail(configuration, ERROR_SUBJECT, ERROR_BODY);
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
