package com.novoda.github.reports.github;

import com.novoda.github.reports.github.issue.Comment;
import com.novoda.github.reports.github.issue.Event;
import com.novoda.github.reports.github.issue.Issue;
import com.novoda.github.reports.github.issue.IssuesServiceClient;
import com.novoda.github.reports.github.repository.RepositoriesServiceClient;
import com.novoda.github.reports.github.repository.Repository;
import com.novoda.github.reports.github.timeline.TimelineEvent;
import com.novoda.github.reports.github.timeline.TimelineServiceClient;

import java.util.Calendar;

import javafx.util.Pair;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.UtilityFunctions;

public class DebugClient {

    public static void getRepositories() {
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

    public static void getIssues() {
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

    protected static void getEvents() {
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

    protected static void getComments() {
        IssuesServiceClient.newInstance()
                .getCommentsFrom("novoda", "github-reports", 36)
                .toBlocking()
                .subscribe(new Subscriber<Comment>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(Comment comment) {
                        System.out.println(comment);
                    }
                });
    }

    protected static void getTimeline() {
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

    public static void getAll() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();

        repositoriesServiceClient.getRepositoriesFrom("novoda")
                .doOnEach(notification -> { /* TODO persist repo */ })
                .flatMap(
                        (Func1<Repository, Observable<Issue>>) repository -> {
                            if (!repository.getFullName().contains("reports")) {
                                return Observable.empty();
                            }
                            return issuesServiceClient.getIssuesFrom("novoda", repository);
                        },
                        (repository, issue) -> {
                            // TODO persist issue
                            if (issue.getNumber() < 32) {
                                return Observable.just(new Pair<Comment, Event>(null,null));
                            }
                            System.out.println("<<<<< getting issue " + issue.getNumber() + " " + issue.getTitle());
                            return issuesServiceClient.getCommentsFrom("novoda", repository.getName(), issue.getNumber())
                                    .zipWith(issuesServiceClient.getEventsFrom("novoda", repository.getName(), issue.getNumber()), Pair::new);
                        }
                )
                .concatMap(UtilityFunctions.identity())
                .toBlocking()
                .subscribe(new Subscriber<Pair<Comment, Event>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> " + e.getMessage());
                    }

                    @Override
                    public void onNext(Pair<Comment, Event> commentEventPair) {
                        // TODO persist comment and event
                        Comment comment = commentEventPair.getKey();
                        Event event = commentEventPair.getValue();

                        if (comment == null || event == null) {
                            return;
                        }

                        System.out.println("> COMMENT: " + comment);
                        System.out.println("> EVENT: " + event);
                    }
                });

    }

    public static void getAllTimelineEvents() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();
        TimelineServiceClient timelineServiceClient = TimelineServiceClient.newInstance();

        repositoriesServiceClient.getRepositoriesFrom("novoda")
                .flatMap(
                        (Func1<Repository, Observable<Issue>>) repository -> {
                            if (!repository.getFullName().contains("reports")) {
                                return Observable.empty();
                            }
                            return issuesServiceClient.getIssuesFrom("novoda", repository);
                        },
                        (Func2<Repository, Issue, Observable<TimelineEvent>>) (repository, issue) -> {
                            if (!repository.getFullName().contains("reports") || issue.getNumber() < 32) {
                                return Observable.empty();
                            }
                            return timelineServiceClient.getTimelineFor("novoda", repository.getName(), issue.getNumber())
                                    .onErrorReturn(throwable -> new TimelineEvent());
                        }
                )
                .concatMap(UtilityFunctions.identity())
                .toBlocking()
                .subscribe(new Subscriber<TimelineEvent>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> COMPLETED!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> ERROR: " + e.getMessage());
                    }

                    @Override
                    public void onNext(TimelineEvent event) {
                        System.out.println("> " + event);
                    }
                });
    }

}
