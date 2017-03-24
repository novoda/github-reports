package com.novoda.github.reports.batch.aws.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.batch.configuration.DatabaseConfiguration;
import com.novoda.github.reports.batch.configuration.GithubConfiguration;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

public class AmazonConfigurationConverter {

    private final Gson gson;

    public static AmazonConfigurationConverter newInstance() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
        return new AmazonConfigurationConverter(gson);
    }

    private AmazonConfigurationConverter(Gson gson) {
        this.gson = gson;
    }

    public AmazonConfiguration fromJson(InputStream json) throws ConfigurationConverterException {
        AmazonRawConfiguration amazonRawConfiguration = jsonToAmazonRawConfiguration(json);

        DatabaseConfiguration databaseConfiguration = toDatabaseConfiguration(amazonRawConfiguration);
        GithubConfiguration githubConfiguration = toGithubConfiguration(amazonRawConfiguration);
        EmailNotifierConfiguration emailNotifierConfiguration = toEmailNotifierConfiguration(amazonRawConfiguration);

        return AmazonConfiguration.create(
                amazonRawConfiguration.jobName(),
                amazonRawConfiguration.alarmName(),
                amazonRawConfiguration.retryCount(),
                databaseConfiguration,
                githubConfiguration,
                emailNotifierConfiguration
        );
    }

    private DatabaseConfiguration toDatabaseConfiguration(AmazonRawConfiguration amazonRawConfiguration) {
        AmazonRawDatabaseConfiguration rawDatabaseConfiguration = amazonRawConfiguration.database();
        return DatabaseConfiguration.create(
                rawDatabaseConfiguration.connectionString(),
                rawDatabaseConfiguration.username(),
                rawDatabaseConfiguration.password()
        );
    }

    private GithubConfiguration toGithubConfiguration(AmazonRawConfiguration amazonRawConfiguration) {
        AmazonRawGithubConfiguration rawGithubConfiguration = amazonRawConfiguration.github();
        return GithubConfiguration.create(rawGithubConfiguration.token());
    }

    private EmailNotifierConfiguration toEmailNotifierConfiguration(AmazonRawConfiguration amazonRawConfiguration) {
        AmazonRawEmailNotifierConfiguration rawEmailNotifierConfiguration = amazonRawConfiguration.email();
        return EmailNotifierConfiguration.create(
                rawEmailNotifierConfiguration.host(),
                rawEmailNotifierConfiguration.port(),
                rawEmailNotifierConfiguration.useSsl(),
                rawEmailNotifierConfiguration.from(),
                rawEmailNotifierConfiguration.username(),
                rawEmailNotifierConfiguration.password(),
                rawEmailNotifierConfiguration.to()
        );
    }

    private AmazonRawConfiguration jsonToAmazonRawConfiguration(InputStream json) throws ConfigurationConverterException {
        try {
            InputStreamReader reader = new InputStreamReader(json);
            return gson.fromJson(reader, AmazonRawConfiguration.class);
        } catch (Exception e) {
            throw new ConfigurationConverterException(e);
        }
    }

    public String toJson(AmazonConfiguration configuration) throws ConfigurationConverterException {
        String jobName = configuration.jobName();
        String alarmName = configuration.alarmName();
        int retryCount = configuration.retryCount();

        AmazonRawDatabaseConfiguration rawDatabaseConfiguration = toRawDatabaseConfiguration(configuration);
        AmazonRawGithubConfiguration githubRawConfiguration = toRawGithubConfiguration(configuration);
        AmazonRawEmailNotifierConfiguration rawEmailNotifierConfiguration = toRawEmailNotifierConfiguration(configuration);

        AmazonRawConfiguration amazonRawConfiguration = AmazonRawConfiguration.builder()
                .jobName(jobName)
                .alarmName(alarmName)
                .retryCount(retryCount)
                .database(rawDatabaseConfiguration)
                .github(githubRawConfiguration)
                .email(rawEmailNotifierConfiguration)
                .build();

        return rawToJsonConfiguration(amazonRawConfiguration);
    }

    private AmazonRawDatabaseConfiguration toRawDatabaseConfiguration(AmazonConfiguration configuration) {
        DatabaseConfiguration databaseConfiguration = configuration.databaseConfiguration();
        return AmazonRawDatabaseConfiguration.builder()
                .connectionString(databaseConfiguration.connectionString())
                .username(databaseConfiguration.username())
                .password(databaseConfiguration.password())
                .build();
    }

    private AmazonRawGithubConfiguration toRawGithubConfiguration(AmazonConfiguration configuration) {
        GithubConfiguration githubConfiguration = configuration.githubConfiguration();
        return AmazonRawGithubConfiguration.builder()
                .token(githubConfiguration.token())
                .build();
    }

    private AmazonRawEmailNotifierConfiguration toRawEmailNotifierConfiguration(AmazonConfiguration configuration) {
        EmailNotifierConfiguration emailNotifierConfiguration = configuration.notifierConfiguration();
        return AmazonRawEmailNotifierConfiguration.builder()
                .host(emailNotifierConfiguration.host())
                .port(emailNotifierConfiguration.port())
                .useSsl(emailNotifierConfiguration.useSsl())
                .from(emailNotifierConfiguration.from())
                .username(emailNotifierConfiguration.username())
                .password(emailNotifierConfiguration.password())
                .to(emailNotifierConfiguration.to())
                .build();
    }

    private String rawToJsonConfiguration(AmazonRawConfiguration amazonRawConfiguration) throws ConfigurationConverterException {
        try {
            return gson.toJson(amazonRawConfiguration);
        } catch (Exception e) {
            throw new ConfigurationConverterException(e);
        }
    }

}
