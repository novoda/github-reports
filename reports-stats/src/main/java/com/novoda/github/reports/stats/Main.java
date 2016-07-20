package com.novoda.github.reports.stats;

import com.beust.jcommander.JCommander;
import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.floatschedule.convert.FloatGithubUserConverter;
import com.novoda.github.reports.data.db.*;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.stats.command.*;
import com.novoda.github.reports.stats.handler.*;

public class Main {

    static {
        LogHelper.turnOffJooqAd();
    }

    private static final String COMMAND_USER = "user";
    private static final String COMMAND_REPO = "repo";
    private static final String COMMAND_PROJECT = "project";
    private static final String COMMAND_PULL_REQUEST = "pr";
    private static final String COMMAND_OVERALL = "overall";

    private void execute(String[] args) throws UnhandledCommandException, OptionsNotValidException {
        UserOptions userOptions = new UserOptions();
        RepoOptions repoOptions = new RepoOptions();
        ProjectOptions projectOptions = new ProjectOptions();
        PullRequestOptions prOptions = new PullRequestOptions();
        OverallOptions overallOptions = new OverallOptions();

        JCommander commander = new JCommander();
        commander.addCommand(COMMAND_USER, userOptions);
        commander.addCommand(COMMAND_REPO, repoOptions);
        commander.addCommand(COMMAND_PROJECT, projectOptions);
        commander.addCommand(COMMAND_PULL_REQUEST, prOptions);
        commander.addCommand(COMMAND_OVERALL, overallOptions);

        commander.parse(args);
        String command = commander.getParsedCommand();

        Stats stats;
        ConnectionManager connectionManager = DbConnectionManager.newInstance();

        if (command.equals(COMMAND_USER)) {
            UserCommandHandler handler = new UserCommandHandler(DbUserDataLayer.newInstance(connectionManager));
            stats = handler.handle(userOptions);
        } else if (command.equals(COMMAND_REPO)) {
            RepoCommandHandler handler = new RepoCommandHandler(DbRepoDataLayer.newInstance(connectionManager));
            stats = handler.handle(repoOptions);
        } else if (command.equals(COMMAND_PROJECT)) {
            ProjectCommandHandler handler = new ProjectCommandHandler(DbProjectDataLayer.newInstance(connectionManager));
            stats = handler.handle(projectOptions);
        } else if (command.equals(COMMAND_PULL_REQUEST)) {
            PullRequestCommandHandler handler = new PullRequestCommandHandler(DbEventDataLayer.newInstance(connectionManager));
            stats = handler.handle(prOptions);
        } else if (command.equals(COMMAND_OVERALL)) {
            DbEventDataLayer eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
            FloatServiceClient floatServiceClient = FloatServiceClient.newInstance();
            FloatGithubUserConverter floatGithubUserConverter = FloatGithubUserConverter.newInstance();
            OverallCommandHandler handler = new OverallCommandHandler(eventDataLayer, floatServiceClient, floatGithubUserConverter);
            stats = handler.handle(overallOptions);
        } else {
            throw new UnhandledCommandException(String.format("The command %s is not supported", command));
        }

        System.out.println(stats.describeStats());
    }

    public static void main(String[] args) throws UnhandledCommandException, OptionsNotValidException {
        new Main().execute(args);
    }
}
