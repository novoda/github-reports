package com.novoda.github.reports.batch.aws.notifier;

import com.novoda.github.reports.batch.aws.configuration.AmazonConfiguration;
import com.novoda.github.reports.batch.aws.configuration.EmailNotifierConfiguration;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;
import com.novoda.github.reports.batch.notifier.NotifierOperationFailedException;
import com.novoda.github.reports.batch.worker.Logger;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailNotifierTest {

    private static final String ANY_JOB_NAME = "some-job";
    private static final String ANY_ALARM_NAME = "some-alarm";
    private static final String ANY_CONN_STRING = "jdbc:mysql://something-something-danger-zone";
    private static final String ANY_USERNAME = "sterling";
    private static final String ANY_PASSWORD = "mawp";
    private static final DatabaseConfiguration DATABASE_CONFIGURATION = DatabaseConfiguration.create(
            ANY_CONN_STRING,
            ANY_USERNAME,
            ANY_PASSWORD
    );
    private static final String ANY_TOKEN = "welcometothedangerzone";
    private static final GithubConfiguration GITHUB_CONFIGURATION = GithubConfiguration.create(ANY_TOKEN);
    private static final String ANY_HOST = "smtp.google.com";
    private static final int ANY_PORT = 1337;
    private static final boolean ANY_USE_SSL = true;
    private static final String ANY_FROM = "sterling@archer.mawp";
    private static final List<String> ANY_TO = Arrays.asList("francesco@novoda.com", "carl@novoda.com");
    private static final EmailNotifierConfiguration EMAIL_NOTIFIER_CONFIGURATION = EmailNotifierConfiguration.create(
            ANY_HOST,
            ANY_PORT,
            ANY_USE_SSL,
            ANY_FROM,
            ANY_USERNAME,
            ANY_PASSWORD,
            ANY_TO
    );
    private static AmazonConfiguration AMAZON_CONFIGURATION = AmazonConfiguration.create(
            ANY_JOB_NAME,
            ANY_ALARM_NAME,
            DATABASE_CONFIGURATION,
            GITHUB_CONFIGURATION,
            EMAIL_NOTIFIER_CONFIGURATION
    );

    @Mock
    private Email email;

    @Mock
    private Logger logger;

    @InjectMocks
    private EmailNotifier notifier;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenValidConfiguration_whenNotifyCompletion_thenSetAllParameters()
            throws NotifierOperationFailedException, EmailException {

        notifier.notifyCompletion(AMAZON_CONFIGURATION);

        verifyEmailSetWithParametersFromConfig();
        verify(email).setSubject("[github-reports] Your job has completed!");
        verify(email).setMsg("The job with name \"" + ANY_JOB_NAME + "\" has completed successfully.");
    }

    @Test
    public void givenValidConfiguration_whenNotifyError_thenSetAllParameters()
            throws NotifierOperationFailedException, EmailException {

        notifier.notifyError(AMAZON_CONFIGURATION, new Exception());

        verifyEmailSetWithParametersFromConfig();
        verify(email).setSubject("[github-reports] Your job has errored!");
        verify(email).setMsg(startsWith("The job with name \"" + ANY_JOB_NAME + "\" has errored."));
    }

    private void verifyEmailSetWithParametersFromConfig() throws EmailException {
        verify(email).setHostName(ANY_HOST);
        verify(email).setSmtpPort(ANY_PORT);
        verify(email).setAuthenticator(any(DefaultAuthenticator.class));
        verify(email).setSSLOnConnect(ANY_USE_SSL);
        verify(email).setFrom(ANY_FROM);
        ArgumentCaptor<String> recipientArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(email, VerificationModeFactory.times(2)).addTo(recipientArgumentCaptor.capture());
        assertEquals(ANY_TO, recipientArgumentCaptor.getAllValues());
    }

    @Test
    public void givenErroringEmailSend_whenNotifyCompletion_thenThrowNotifierOperationFailedException()
            throws NotifierOperationFailedException, EmailException {

        when(email.send()).thenThrow(EmailException.class);

        expectedException.expect(NotifierOperationFailedException.class);
        notifier.notifyCompletion(AMAZON_CONFIGURATION);
    }

    @Test
    public void givenErroringEmailSend_whenNotifyError_thenThrowNotifierOperationFailedException()
            throws NotifierOperationFailedException, EmailException {

        when(email.send()).thenThrow(EmailException.class);

        expectedException.expect(NotifierOperationFailedException.class);
        notifier.notifyError(AMAZON_CONFIGURATION, new Exception());
    }

}
