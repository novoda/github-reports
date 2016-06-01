package com.novoda.github.reports.batch;

import com.novoda.github.reports.batch.network.RateLimitRemainingCounter;
import com.novoda.github.reports.batch.network.RateLimitRemainingCounterContainer;
import com.novoda.github.reports.batch.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.batch.network.RateLimitResetRepository;

import java.util.Date;

public class Main {

    private void execute(String[] args) {
        System.out.println("*** STARTED...");

        RateLimitRemainingCounter remainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository resetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();

        DebugClient.retrieveRepositories();

        System.out.println("x Remaining number of requests: " + remainingCounter.get());
        System.out.println("x Reset time: " + resetRepository.getNextResetTime() + ", " + new Date(resetRepository.getNextResetTime() * 1000L));
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }
}
