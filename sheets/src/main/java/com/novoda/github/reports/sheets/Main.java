package com.novoda.github.reports.sheets;

import com.novoda.github.reports.sheets.network.ProjectSheetsServiceClient;

import rx.schedulers.Schedulers;

public class Main {

    public static void main(String[] args) {

        ProjectSheetsServiceClient sheetsServiceClient = ProjectSheetsServiceClient.newInstance();
        sheetsServiceClient.getProjectEntries()
                .doOnNext(System.out::println)
                .subscribeOn(Schedulers.immediate())
                .subscribe();

    }

}
