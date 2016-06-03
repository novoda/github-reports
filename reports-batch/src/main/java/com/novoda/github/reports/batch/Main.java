package com.novoda.github.reports.batch;

import com.novoda.github.reports.batch.network.RateLimitRemainingCounter;
import com.novoda.github.reports.batch.network.RateLimitRemainingCounterContainer;
import com.novoda.github.reports.batch.network.RateLimitRemainingResetRepositoryContainer;
import com.novoda.github.reports.batch.network.RateLimitResetRepository;

import java.util.Calendar;
import java.util.Date;

public class Main {

    private void execute(String[] args) {
        System.out.println("*** STARTED...");

        RateLimitRemainingCounter remainingCounter = RateLimitRemainingCounterContainer.getInstance();
        RateLimitResetRepository resetRepository = RateLimitRemainingResetRepositoryContainer.getInstance();

        // 2015-08-07T15:06:58Z
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, Calendar.AUGUST, 7, 15, 00, 30);
        DebugClient.getEvents(calendar.getTime());

        System.out.println("x Remaining number of requests: " + remainingCounter.get());
        System.out.println("x Reset time: " + resetRepository.getNextResetTime() + ", " + new Date(resetRepository.getNextResetTime() * 1000L));
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }
}
