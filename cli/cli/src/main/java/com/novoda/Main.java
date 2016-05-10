package com.novoda;

import com.beust.jcommander.JCommander;
import com.novoda.command.ProjectOptions;
import com.novoda.command.RepoOptions;
import com.novoda.command.UserOptions;
import com.novoda.core.mock.MockProjectDataLayer;
import com.novoda.core.mock.MockRepoDataLayer;
import com.novoda.core.mock.MockUserDataLayer;
import com.novoda.core.stats.Stats;
import com.novoda.handler.ProjectCommandHandler;
import com.novoda.handler.RepoCommandHandler;
import com.novoda.handler.UserCommandHandler;

public class Main {

    private static final String COMMAND_USER = "user";
    private static final String COMMAND_REPO = "repo";
    private static final String COMMAND_PROJECT = "project";

    private void execute(String[] args) throws UnhandledCommandException {
        UserOptions userOptions = new UserOptions();
        RepoOptions repoOptions = new RepoOptions();
        ProjectOptions projectOptions = new ProjectOptions();

        JCommander commander = new JCommander();
        commander.addCommand(COMMAND_USER, userOptions);
        commander.addCommand(COMMAND_REPO, repoOptions);
        commander.addCommand(COMMAND_PROJECT, projectOptions);

        commander.parse(args);
        String command = commander.getParsedCommand();

        Stats stats;

        if (command.equals(COMMAND_USER)) {
            UserCommandHandler handler = new UserCommandHandler(new MockUserDataLayer());
            stats = handler.handle(userOptions);
        } else if (command.equals(COMMAND_REPO)) {
            RepoCommandHandler handler = new RepoCommandHandler(new MockRepoDataLayer());
            stats = handler.handle(repoOptions);
        } else if (command.equals(COMMAND_PROJECT)) {
            ProjectCommandHandler handler = new ProjectCommandHandler(new MockProjectDataLayer());
            stats = handler.handle(projectOptions);
        } else {
            throw new UnhandledCommandException(String.format("The command %s is not supported", command));
        }

        System.out.println(stats.describeStats());
    }

    public static void main(String[] args) throws UnhandledCommandException {
	   new Main().execute(args);
    }
}
