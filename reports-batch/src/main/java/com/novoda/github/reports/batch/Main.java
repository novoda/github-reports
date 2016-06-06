package com.novoda.github.reports.batch;

import com.novoda.github.reports.batch.issue.Comment;
import com.novoda.github.reports.batch.network.RateLimitRemainingCounter;
import com.novoda.github.reports.batch.network.RateLimitRemainingCounterContainer;
import com.novoda.github.reports.batch.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.batch.network.RateLimitResetRepository;
import com.novoda.github.reports.batch.pullrequest.GithubPullRequestService;

import java.util.Date;

import rx.Subscriber;

public class Main {

    private void execute(String[] args) {
        System.out.println("*** STARTED...");

        RateLimitRemainingCounter remainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository resetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();

        //DebugClient.retrieveIssuesAndEvents("novoda", 25308952L, "github-reports");
        GithubPullRequestService.newInstance().getReviewCommentsForPullRequestFor("novoda", "all-4", 3033, null)
                .toBlocking()
                .subscribe(new Subscriber<Comment>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Comment comment) {
                        System.out.println(comment);
                    }
                });

        System.out.println("x Remaining number of requests: " + remainingCounter.get());
        System.out.println("x Reset time: " + resetRepository.getNextResetTime() + ", " + new Date(resetRepository.getNextResetTime() * 1000L));
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }
}
