package com.novoda.github.reports;

import com.novoda.github.reports.batch.DebugClient;
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

        DebugClient.getAllFilteringOutEverythingBut("github-reports", 32);

        System.out.println("x Remaining number of requests: " + remainingCounter.get());
        System.out.println("x Reset time: " + resetRepository.get() + ", " + new Date(resetRepository.get() * 1000L));
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }
}
