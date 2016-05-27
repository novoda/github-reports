package com.novoda.github.reports.batch.github;

import com.novoda.github.reports.batch.github.issue.Comment;
import com.novoda.github.reports.batch.github.issue.Event;
import com.novoda.github.reports.batch.github.issue.Issue;
import com.novoda.github.reports.batch.github.issue.IssuesServiceClient;
import com.novoda.github.reports.batch.github.repository.RepositoriesServiceClient;
import com.novoda.github.reports.batch.github.repository.Repository;
import com.novoda.github.reports.batch.github.timeline.TimelineEvent;
import com.novoda.github.reports.batch.github.timeline.TimelineServiceClient;

import java.util.Calendar;

import javafx.util.Pair;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.UtilityFunctions;

public class DebugClient {

    private DebugClient() {
        // non-instantiable
    }

    public static void getRepositories() {
        RepositoriesServiceClient.newInstance()
                .getRepositoriesFrom("novoda")
                .toBlocking()
                .subscribe(new Subscriber<Repository>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> getRepositories completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getRepositories error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Repository repository) {
                        System.out.println("> getRepositories: " + repository.getFullName());
                    }
                });
    }

    public static void getIssues() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.MAY, 24, 13, 30, 30);
        IssuesServiceClient.newInstance()
                .getIssuesFrom("novoda", "all-4", calendar.getTime())
                .toBlocking()
                .subscribe(new Subscriber<Issue>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> getIssues completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getIssues error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Issue issue) {
                        System.out.println("> getIssues: " + issue);
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
                        System.out.println(">>>>> getEvents completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getEvents error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Event event) {
                        System.out.println("> getEvents: " + event);
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
                        System.out.println(">>>>> getComments completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getComments error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Comment comment) {
                        System.out.println("> getComments: " + comment);
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
                        System.out.println(">>>>> getTimeline completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getTimeline error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(TimelineEvent timelineEvent) {
                        System.out.println("> getTimeline: " + timelineEvent);
                    }
                });
    }

    public static void getAll() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();

        repositoriesServiceClient.getRepositoriesFrom("novoda")
                .doOnEach(notification -> { /* TODO persist repo */ })
                .flatMap(
                        (Func1<Repository, Observable<Issue>>) repository -> issuesServiceClient.getIssuesFrom("novoda", repository),
                        (repository, issue) -> /* TODO persist issue */
                                issuesServiceClient.getCommentsFrom("novoda", repository.getName(), issue.getNumber())
                                    .zipWith(issuesServiceClient.getEventsFrom("novoda", repository.getName(), issue.getNumber()), Pair::new))
                .concatMap(UtilityFunctions.identity())
                .toBlocking()
                .subscribe(new Subscriber<Pair<Comment, Event>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> getAll completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getAll error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Pair<Comment, Event> commentEventPair) {
                        // TODO persist comment and event
                        Comment comment = commentEventPair.getKey();
                        Event event = commentEventPair.getValue();
                        System.out.println("> getAll comment: " + comment);
                        System.out.println("> getAll event: " + event);
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
                        System.out.println(">>>>> getAllTimelineEvents completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> getAllTimelineEvents error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(TimelineEvent event) {
                        System.out.println("> getAllTimelineEvents: " + event);
                    }
                });
    }

}
