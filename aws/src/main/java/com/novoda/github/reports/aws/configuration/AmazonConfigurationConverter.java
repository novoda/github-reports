package com.novoda.github.reports.aws.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

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

    public AmazonConfiguration fromJson(String json) throws ConfigurationConverterException {
        AmazonRawConfiguration amazonRawConfiguration = jsonToAmazonRawConfiguration(json);

        AmazonRawDatabaseConfiguration rawDatabaseConfiguration = amazonRawConfiguration.database();
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.create(
                rawDatabaseConfiguration.connectionString(),
                rawDatabaseConfiguration.username(),
                rawDatabaseConfiguration.password()
        );

        AmazonRawGithubConfiguration rawGithubConfiguration = amazonRawConfiguration.github();
        GithubConfiguration githubConfiguration = GithubConfiguration.create(rawGithubConfiguration.token());

        AmazonRawEmailNotifierConfiguration rawEmailNotifierConfiguration = amazonRawConfiguration.email();
        EmailNotifierConfiguration emailNotifierConfiguration = EmailNotifierConfiguration.create(
                rawEmailNotifierConfiguration.host(),
                rawEmailNotifierConfiguration.port(),
                rawEmailNotifierConfiguration.useSSL(),
                rawEmailNotifierConfiguration.from(),
                rawEmailNotifierConfiguration.username(),
                rawEmailNotifierConfiguration.password()
        );

        return AmazonConfiguration.create(
                amazonRawConfiguration.jobName(),
                databaseConfiguration,
                githubConfiguration,
                emailNotifierConfiguration
        );
    }

    private AmazonRawConfiguration jsonToAmazonRawConfiguration(String json) throws ConfigurationConverterException {
        try {
            return gson.fromJson(json, AmazonRawConfiguration.class);
        } catch (Exception e) {
            throw new ConfigurationConverterException(e);
        }
    }

    public String toJson(AmazonConfiguration configuration) throws ConfigurationConverterException {
        String jobName = configuration.jobName();

        DatabaseConfiguration databaseConfiguration = configuration.databaseConfiguration();
        AmazonRawDatabaseConfiguration rawDatabaseConfiguration = AmazonRawDatabaseConfiguration.builder()
                .connectionString(databaseConfiguration.connectionString())
                .username(databaseConfiguration.username())
                .password(databaseConfiguration.password())
                .build();

        GithubConfiguration githubConfiguration = configuration.githubConfiguration();
        AmazonRawGithubConfiguration githubRawConfiguration = AmazonRawGithubConfiguration.builder()
                .token(githubConfiguration.token())
                .build();

        EmailNotifierConfiguration emailNotifierConfiguration = configuration.notifierConfiguration();
        AmazonRawEmailNotifierConfiguration rawEmailNotifierConfiguration = AmazonRawEmailNotifierConfiguration.builder()
                .host(emailNotifierConfiguration.host())
                .port(emailNotifierConfiguration.port())
                .useSSL(emailNotifierConfiguration.useSSL())
                .from(emailNotifierConfiguration.from())
                .username(emailNotifierConfiguration.username())
                .password(emailNotifierConfiguration.password())
                .build();

        AmazonRawConfiguration amazonRawConfiguration = AmazonRawConfiguration.builder()
                .jobName(jobName)
                .database(rawDatabaseConfiguration)
                .github(githubRawConfiguration)
                .email(rawEmailNotifierConfiguration)
                .build();

        return rawToJsonConfiguration(amazonRawConfiguration);
    }

    private String rawToJsonConfiguration(AmazonRawConfiguration amazonRawConfiguration) throws ConfigurationConverterException {
        try {
            return gson.toJson(amazonRawConfiguration);
        } catch (Exception e) {
            throw new ConfigurationConverterException(e);
        }
    }

}
