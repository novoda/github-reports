package com.novoda.github.reports.stats;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.db.DbProjectDataLayer;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.db.LogHelper;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.stats.command.OptionsNotValidException;
import com.novoda.github.reports.stats.command.ProjectOptions;
import com.novoda.github.reports.stats.command.PullRequestOptions;
import com.novoda.github.reports.stats.command.PullRequestOptionsValidator;
import com.novoda.github.reports.stats.command.RepoOptions;
import com.novoda.github.reports.stats.command.UserOptions;
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

    private void execute(String[] args) throws UnhandledCommandException, OptionsNotValidException {
        UserOptions userOptions = new UserOptions();
        RepoOptions repoOptions = new RepoOptions();
        ProjectOptions projectOptions = new ProjectOptions();
        PullRequestOptions prOptions = new PullRequestOptions();

        JCommander commander = new JCommander();
        commander.addCommand(COMMAND_USER, userOptions);
        commander.addCommand(COMMAND_REPO, repoOptions);
        commander.addCommand(COMMAND_PROJECT, projectOptions);
        commander.addCommand(COMMAND_PULL_REQUEST, prOptions);

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
            PullRequestOptionsValidator validator = new PullRequestOptionsValidator();
            validatePrOptions(prOptions, validator);
            PullRequestCommandHandler handler = new PullRequestCommandHandler(DbEventDataLayer.newInstance(connectionManager));
            stats = handler.handle(prOptions);
        } else {
            throw new UnhandledCommandException(String.format("The command %s is not supported", command));
        }

        System.out.println(stats.describeStats());
    }

    private void validatePrOptions(PullRequestOptions prOptions, PullRequestOptionsValidator validator) throws OptionsNotValidException {
        boolean isValid = validator.validate(prOptions);
        if (!isValid) {
            throw new OptionsNotValidException("You can't specify both projects and repositories in the options.");
        }
    }

    public static void main(String[] args) throws UnhandledCommandException, OptionsNotValidException {
        new Main().execute(args);
    }
}
