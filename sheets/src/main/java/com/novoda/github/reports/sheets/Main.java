package com.novoda.github.reports.sheets;

import com.novoda.github.reports.sheets.network.SheetsServiceClient;

import rx.schedulers.Schedulers;

public class Main {

    public static void main(String[] args) {

        SheetsServiceClient sheetsServiceClient = SheetsServiceClient.newInstance();
        sheetsServiceClient.getDocument()
                .doOnNext(System.out::println)
                .subscribeOn(Schedulers.immediate())
                .subscribe();

    }

}
