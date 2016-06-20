package com.novoda.github.reports.batch;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.batch.command.BatchOptions;
import com.novoda.github.reports.batch.local.BatchClient;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.network.RateLimitRemainingCounter;
import com.novoda.github.reports.service.network.RateLimitRemainingCounterContainer;
import com.novoda.github.reports.service.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.service.network.RateLimitResetRepository;
import com.novoda.github.reports.data.db.LogHelper;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscription;

public class Main {

    static {
        LogHelper.turnOffJooqAd();
    }

    private void execute(String[] args) {
        BatchOptions options = new BatchOptions();
        JCommander commander = new JCommander(options);
        commander.parse(args);

        RateLimitRemainingCounter remainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository resetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();

        Observable<RepositoryIssueEvent> events = BatchClient.retrieve(options.getOrganisation(), options.getFrom());

        CountDownLatch latch = new CountDownLatch(1);

        Subscription subscription = events.subscribe(
                (event) -> System.out.println("> retrieve: " + event),
                (error) -> {
                    System.err.println("> error: " + error.getMessage());
                    error.printStackTrace();
                },
                () -> {
                    System.out.println("> complete");
                    latch.countDown();
                }
        );

        while (!subscription.isUnsubscribed()) {
            try {
                latch.await();
            } catch (InterruptedException ignored) {
                // ignored
            }
        }

        System.out.println("Remaining number of requests: " + remainingCounter.get());
        System.out.println("Reset time: " + resetRepository.getNextResetTime() + ", " + new Date(resetRepository.getNextResetTime() * 1000L));
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }
}
