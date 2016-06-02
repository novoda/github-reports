package com.novoda.github.reports.batch;

import com.novoda.github.reports.batch.issue.Comment;
import com.novoda.github.reports.batch.issue.Event;
import com.novoda.github.reports.batch.issue.Issue;
import com.novoda.github.reports.batch.issue.IssuesServiceClient;
import com.novoda.github.reports.batch.repository.RepositoriesServiceClient;
import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.batch.timeline.TimelineEvent;
import com.novoda.github.reports.batch.timeline.TimelineServiceClient;

import java.util.Calendar;

import javafx.util.Pair;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.UtilityFunctions;

class DebugClient {

    private DebugClient() {
        // non-instantiable
    }

    static void retrieveRepositories() {
        RepositoriesServiceClient.newInstance()
                .retrieveRepositoriesFrom("novoda")
                .toBlocking()
                .subscribe(new Subscriber<Repository>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(">>>>> retrieveRepositories completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(">>>>> retrieveRepositories error: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Repository repository) {
                        System.out.println("> retrieveRepositories: " + repository.getFullName());
                    }
                });
    }

    static void getIssues() {
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

    static void getEvents() {
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

    static void getComments() {
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

    static void getTimeline() {
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

    static void getAll() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();

        repositoriesServiceClient.retrieveRepositoriesFrom("novoda")
                .doOnEach(notification -> { /* TODO persist repo */ })
                .flatMap(
                        (Func1<Repository, Observable<Issue>>) repository -> issuesServiceClient.getIssuesFrom("novoda", repository),
                        (repository, issue) ->
                                /* TODO persist issue */
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

    static void getAllFilteringOutEverythingBut(String repositoryName, Integer issueNumber) {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();

        repositoriesServiceClient.retrieveRepositoriesFrom("novoda")
                .doOnEach(notification -> {
                    /* TODO persist repo */
                })
                .flatMap(
                        (Func1<Repository, Observable<Issue>>) repository -> {
                            if (!repository.getFullName().contains(repositoryName)) {
                                return Observable.empty();
                            }
                            return issuesServiceClient.getIssuesFrom("novoda", repository);
                        },
                        (repository, issue) -> {
                            /* TODO persist issue */
                            if (issue.getNumber() != issueNumber) {
                                return Observable.just(new Pair<Comment, Event>(null, null));
                            }
                            return issuesServiceClient.getCommentsFrom("novoda", repository.getName(), issue.getNumber())
                                    .zipWith(issuesServiceClient.getEventsFrom("novoda", repository.getName(), issue.getNumber()), Pair::new);
                        }
                )
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
                        if (comment == null || event == null) {
                            return;
                        }
                        System.out.println("> getAll comment: " + comment);
                        System.out.println("> getAll event: " + event);
                    }
                });
    }

    static void getAllTimelineEvents() {
        RepositoriesServiceClient repositoriesServiceClient = RepositoriesServiceClient.newInstance();
        IssuesServiceClient issuesServiceClient = IssuesServiceClient.newInstance();
        TimelineServiceClient timelineServiceClient = TimelineServiceClient.newInstance();

        repositoriesServiceClient.retrieveRepositoriesFrom("novoda")
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
