package com.novoda.github.reports;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.command.ProjectOptions;
import com.novoda.github.reports.command.RepoOptions;
import com.novoda.github.reports.command.UserOptions;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.DbProjectDataLayer;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.db.DbUserDataLayer;
import com.novoda.github.reports.data.model.Stats;
import com.novoda.github.reports.github.issue.Event;
import com.novoda.github.reports.github.issue.Issue;
import com.novoda.github.reports.github.issue.IssuesServiceClient;
import com.novoda.github.reports.github.repository.RepositoriesServiceClient;
import com.novoda.github.reports.github.repository.Repository;
import com.novoda.github.reports.github.timeline.TimelineEvent;
import com.novoda.github.reports.github.timeline.TimelineServiceClient;
import com.novoda.github.reports.handler.ProjectCommandHandler;
import com.novoda.github.reports.handler.RepoCommandHandler;
import com.novoda.github.reports.handler.UserCommandHandler;

import java.util.Calendar;

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
        ConnectionManager connectionManager = DbConnectionManager.newInstance();

        if (command.equals(COMMAND_USER)) {
            UserCommandHandler handler = new UserCommandHandler(new DbUserDataLayer(connectionManager));
            stats = handler.handle(userOptions);
        } else if (command.equals(COMMAND_REPO)) {
            RepoCommandHandler handler = new RepoCommandHandler(new DbRepoDataLayer(connectionManager));
            stats = handler.handle(repoOptions);
        } else if (command.equals(COMMAND_PROJECT)) {
            ProjectCommandHandler handler = new ProjectCommandHandler(new DbProjectDataLayer(connectionManager));
            stats = handler.handle(projectOptions);
        } else {
            throw new UnhandledCommandException(String.format("The command %s is not supported", command));
        }

        System.out.println(stats.describeStats());
    }

    public static void main(String[] args) throws UnhandledCommandException {
        //new Main().execute(args);
        getEvents();
    }

    private static void getRepositories() {
        RepositoriesServiceClient.newInstance()
                .getRepositoriesFrom("novoda")
                .toBlocking()
                .subscribe(new Subscriber<Repository>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(Repository repository) {
                        System.out.println(repository.getFullName());
                    }
                });
    }

    private static void getIssues() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.MAY, 24, 13, 30, 30);
        IssuesServiceClient.newInstance()
                //.getIssuesFrom("novoda", "all-4", calendar.getTime())
                .getIssuesFrom("novoda", "accessibilitools")
                .toBlocking()
                .subscribe(new Subscriber<Issue>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(Issue issue) {
                        System.out.println(issue);
                    }
                });
    }

    private static void getEvents() {
        IssuesServiceClient.newInstance()
                .getEventsFrom("novoda", "github-reports", 36)
                .toBlocking()
                .subscribe(new Subscriber<Event>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(Event event) {
                        System.out.println(event);
                    }
                });
    }

    private static void getTimeline() {
        TimelineServiceClient.newInstance()
                .getTimelineFor("novoda", "github-reports", 36)
                .toBlocking()
                .subscribe(new Subscriber<TimelineEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(TimelineEvent timelineEvent) {
                        System.out.println(timelineEvent);
                    }
                });
    }
}
