package com.novoda.github.reports.stats;

import com.beust.jcommander.JCommander;
import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatGithubProjectConverter;
import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.floatschedule.convert.GithubUserConverter;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.db.LogHelper;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.stats.command.AggregateOptions;
import com.novoda.github.reports.stats.command.OptionsNotValidException;
import com.novoda.github.reports.stats.command.OverallOptions;
import com.novoda.github.reports.stats.command.ProjectOptions;
import com.novoda.github.reports.stats.command.PullRequestOptions;
import com.novoda.github.reports.stats.command.RepoOptions;
import com.novoda.github.reports.stats.command.UserOptions;
import com.novoda.github.reports.stats.handler.AggregateCommandHandler;
import com.novoda.github.reports.stats.handler.OverallCommandHandler;
import com.novoda.github.reports.stats.handler.ProjectCommandHandler;
import com.novoda.github.reports.stats.handler.PullRequestCommandHandler;
import com.novoda.github.reports.stats.handler.RepoCommandHandler;
import com.novoda.github.reports.stats.handler.UserCommandHandler;

public class Main {

    static {
        LogHelper.turnOffJooqAd();
    }

    private static final String COMMAND_USER = "user";
    private static final String COMMAND_REPO = "repo";
    private static final String COMMAND_PROJECT = "project";
    private static final String COMMAND_PULL_REQUEST = "pr";
    private static final String COMMAND_OVERALL = "overall";
    private static final String COMMAND_AGGREGATE = "aggregate";

    private void execute(String[] args) throws UnhandledCommandException, OptionsNotValidException {
        UserOptions userOptions = new UserOptions();
        RepoOptions repoOptions = new RepoOptions();
        ProjectOptions projectOptions = new ProjectOptions();
        PullRequestOptions prOptions = new PullRequestOptions();
        OverallOptions overallOptions = new OverallOptions();
        AggregateOptions aggregateOptions = new AggregateOptions();

        JCommander commander = new JCommander();
        commander.addCommand(COMMAND_USER, userOptions);
        commander.addCommand(COMMAND_REPO, repoOptions);
        commander.addCommand(COMMAND_PROJECT, projectOptions);
        commander.addCommand(COMMAND_PULL_REQUEST, prOptions);
        commander.addCommand(COMMAND_OVERALL, overallOptions);
        commander.addCommand(COMMAND_AGGREGATE, aggregateOptions);

        commander.parse(args);
        String command = commander.getParsedCommand();

        Stats stats;
        ConnectionManager connectionManager = DbConnectionManager.newInstance();

        if (command.equals(COMMAND_USER)) {
            DbUserDataLayer userDataLayer = DbUserDataLayer.newInstance(connectionManager);
            UserCommandHandler handler = new UserCommandHandler(userDataLayer);
            stats = handler.handle(userOptions);
        } else if (command.equals(COMMAND_REPO)) {
            DbRepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
            RepoCommandHandler handler = new RepoCommandHandler(repoDataLayer);
            stats = handler.handle(repoOptions);
        } else if (command.equals(COMMAND_PROJECT)) {
            DbRepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
            FloatGithubProjectConverter floatGithubProjectConverter = FloatGithubProjectConverter.newInstance();
            ProjectCommandHandler handler = new ProjectCommandHandler(repoDataLayer, floatGithubProjectConverter);
            stats = handler.handle(projectOptions);
        } else if (command.equals(COMMAND_PULL_REQUEST)) {
            DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
            GithubUserConverter floatGithubUserConverter = FloatGithubUserConverter.newInstance();
            PullRequestCommandHandler handler = new PullRequestCommandHandler(eventDataLayer, floatGithubUserConverter);
            stats = handler.handle(prOptions);
        } else if (command.equals(COMMAND_OVERALL)) {
            DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
            FloatServiceClient floatServiceClient = FloatServiceClient.newInstance();

            OverallCommandHandler handler = new OverallCommandHandler(
                    eventDataLayer,
                    floatServiceClient
            );
            stats = handler.handle(overallOptions);
        } else if (command.equals(COMMAND_AGGREGATE)) {
            DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
            FloatServiceClient floatServiceClient = FloatServiceClient.newInstance();

            AggregateCommandHandler handler = new AggregateCommandHandler(
                    eventDataLayer,
                    floatServiceClient
            );
            stats = handler.handle(aggregateOptions);
        } else {
            throw new UnhandledCommandException(String.format("The command %s is not supported", command));
        }

        System.out.println(stats.describeStats());
    }

    public static void main(String[] args) throws UnhandledCommandException, OptionsNotValidException {
        new Main().execute(args);
    }
}
