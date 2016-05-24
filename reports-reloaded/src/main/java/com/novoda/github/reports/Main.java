package com.novoda.github.reports;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.command.ProjectOptions;
import com.novoda.github.reports.command.RepoOptions;
import com.novoda.github.reports.command.UserOptions;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.mock.MockProjectDataLayer;
import com.novoda.github.reports.data.mock.MockUserDataLayer;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.github.repository.RepositoriesServiceClient;
import com.novoda.github.reports.github.repository.Repository;
import com.novoda.github.reports.handler.ProjectCommandHandler;
import com.novoda.github.reports.handler.RepoCommandHandler;
import com.novoda.github.reports.handler.UserCommandHandler;

import rx.Subscriber;

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
            RepoCommandHandler handler = new RepoCommandHandler(new DbRepoDataLayer());
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
        //new Main().execute(args);

        RepositoriesServiceClient.newInstance().getRepositoriesFrom("novoda").toBlocking().subscribe(new Subscriber<Repository>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(Repository repository) {
                System.out.println(repository.getFullName());
            }
        });
    }

}
