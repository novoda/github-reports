package com.novoda.github.reports.batch.handler;

import com.novoda.github.reports.batch.command.LocalBatchOptions;
import com.novoda.github.reports.batch.local.LocalBatchClient;
import com.novoda.github.reports.service.issue.RepositoryIssueEvent;
import com.novoda.github.reports.service.network.RateLimitRemainingCounter;
import com.novoda.github.reports.service.network.RateLimitRemainingCounterContainer;
import com.novoda.github.reports.service.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.service.network.RateLimitResetRepository;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import rx.Observable;
import rx.Subscription;

public class LocalCommandHandler implements CommandHandler<LocalBatchOptions> {

    @Override
    public void handle(LocalBatchOptions options) {
        RateLimitRemainingCounter remainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository resetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();

        Observable<RepositoryIssueEvent> events = LocalBatchClient.retrieve(options.getOrganisation(), options.getFrom());

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
        System.out.println("Reset time: " + resetRepository.getNextResetTime() + ", " + new Date(resetRepository.getNextResetTime()));
    }

}
