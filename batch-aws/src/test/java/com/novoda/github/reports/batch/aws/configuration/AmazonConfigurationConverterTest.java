package com.novoda.github.reports.batch.aws.configuration;

import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;

public class AmazonConfigurationConverterTest {

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
    private static AmazonConfiguration EXPECTED_CONFIGURATION = AmazonConfiguration.create(
            ANY_JOB_NAME,
            ANY_ALARM_NAME,
            DATABASE_CONFIGURATION,
            GITHUB_CONFIGURATION,
            EMAIL_NOTIFIER_CONFIGURATION
    );

    AmazonConfigurationConverter converter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        converter = AmazonConfigurationConverter.newInstance();
    }

    @Test
    public void givenValidJson_whenFromJson_thenReturnCorrectObject() throws IOException, ConfigurationConverterException {
        InputStream json = givenJsonFromResource("configuration.json");

        AmazonConfiguration actual = converter.fromJson(json);

        assertEquals(EXPECTED_CONFIGURATION, actual);
    }

    @Test
    public void givenJsonWithMissingObject_whenFromJson_thenThrowConfigurationConverterException() throws IOException, ConfigurationConverterException {
        InputStream json = givenJsonFromResource("configuration_all_missing.json");

        expectedException.expect(ConfigurationConverterException.class);
        converter.fromJson(json);
    }

    @Test
    public void givenEmptyJson_whenFromJson_thenThrowConfigurationConverterException() throws IOException, ConfigurationConverterException {
        InputStream json = givenJsonFromResource("configuration_empty.json");

        expectedException.expect(ConfigurationConverterException.class);
        converter.fromJson(json);
    }

    @Test
    public void givenValidJsonWithNullAlarmName_whenFromJson_thenDoNotThrowConfigurationConverterExceptionAndVerifyHasNoAlarm() throws ConfigurationConverterException, IOException {
        InputStream json = givenJsonFromResource("configuration_no_alarm.json");

        AmazonConfiguration actual = converter.fromJson(json);

        assertEquals(false, actual.hasAlarm());
    }

    private InputStream givenJsonFromResource(String resource) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

}
