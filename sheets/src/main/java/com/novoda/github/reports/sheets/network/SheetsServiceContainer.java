package com.novoda.github.reports.sheets.network;

public class SheetsServiceContainer {

    private static final SheetsApiService sheetsService = SheetsServiceFactory.newCachingInstance().createService();

    private SheetsServiceContainer() {
        // no op
    }

    public static SheetsApiService getSheetsService() {
        return sheetsService;
    }
}
