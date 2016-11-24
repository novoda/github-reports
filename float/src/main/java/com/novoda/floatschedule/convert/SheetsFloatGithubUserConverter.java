package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.SheetsServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

import static com.novoda.floatschedule.convert.FloatNameFilter.byFloatName;
import static com.novoda.floatschedule.convert.GithubUsernameFilter.byGithubUsername;
import static com.novoda.floatschedule.convert.NoMatchFoundException.noMatchFoundExceptionFor;

public class SheetsFloatGithubUserConverter implements GithubUserConverter {

    private final Map<String, String> floatToGithubUser;
    private final Scheduler scheduler;
    private final SheetsServiceClient sheetsServiceClient;

    public static SheetsFloatGithubUserConverter newInstance() {
        Scheduler scheduler = Schedulers.immediate();
        SheetsServiceClient sheetsServiceClient = SheetsServiceClient.newInstance();
        return new SheetsFloatGithubUserConverter(sheetsServiceClient, scheduler);
    }

    SheetsFloatGithubUserConverter(SheetsServiceClient sheetsServiceClient, Scheduler scheduler) {
        floatToGithubUser = new HashMap<>();
        this.scheduler = scheduler;
        this.sheetsServiceClient = sheetsServiceClient;
    }

    @Override
    public List<String> getGithubUsers() throws Exception {
        readIfNeeded();
        return floatToGithubUser.values()
                .stream()
                .collect(Collectors.toList());
    }

    private void readIfNeeded() {
        if (!floatToGithubUser.isEmpty()) {
            return;
        }
        sheetsServiceClient.getEntries()
                .subscribeOn(scheduler)
                .subscribe(entry -> floatToGithubUser.put(entry.getTitle(), entry.getContent()));
    }

    @Override
    public String getFloatUser(String githubUsername) throws Exception {
        readIfNeeded();
        return floatToGithubUser.entrySet()
                .stream()
                .filter(byGithubUsername(githubUsername))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(noMatchFoundExceptionFor(githubUsername));
    }

    @Override
    public String getGithubUser(String floatName) throws Exception {
        readIfNeeded();
        return floatToGithubUser.entrySet()
                .stream()
                .filter(byFloatName(floatName))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(noMatchFoundExceptionFor(floatName));
    }

}
