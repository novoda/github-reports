package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.UserSheetsServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import rx.schedulers.Schedulers;

import static com.novoda.floatschedule.convert.FloatNameFilter.byFloatName;
import static com.novoda.floatschedule.convert.GithubUsernameFilter.byGithubUsername;
import static com.novoda.floatschedule.convert.NoMatchFoundException.noMatchFoundExceptionFor;

public class SheetsFloatGithubUserConverter {

    private final Map<String, String> floatToGithubUser;
    private final UserSheetsServiceClient userSheetsServiceClient;

    public static SheetsFloatGithubUserConverter newInstance() {
        UserSheetsServiceClient userSheetsServiceClient = UserSheetsServiceClient.newInstance();
        return new SheetsFloatGithubUserConverter(userSheetsServiceClient);
    }

    SheetsFloatGithubUserConverter(UserSheetsServiceClient userSheetsServiceClient) {
        floatToGithubUser = new HashMap<>();
        this.userSheetsServiceClient = userSheetsServiceClient;
    }

    public List<String> getGithubUsers() {
        readIfNeeded();
        return floatToGithubUser.values()
                .stream()
                .collect(Collectors.toList());
    }

    private void readIfNeeded() {
        if (!floatToGithubUser.isEmpty()) {
            return;
        }
        userSheetsServiceClient.getUserEntries()
                .subscribeOn(Schedulers.immediate())
                .subscribe(entry -> floatToGithubUser.put(entry.getTitle(), entry.getContent()));
    }

    public String getFloatUser(String githubUsername) throws NoMatchFoundException {
        readIfNeeded();
        return floatToGithubUser.entrySet()
                .stream()
                .filter(byGithubUsername(githubUsername))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(noMatchFoundExceptionFor(githubUsername));
    }

    public String getGithubUser(String floatName) throws NoMatchFoundException {
        readIfNeeded();
        return floatToGithubUser.entrySet()
                .stream()
                .filter(byFloatName(floatName))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(noMatchFoundExceptionFor(floatName));
    }

}
