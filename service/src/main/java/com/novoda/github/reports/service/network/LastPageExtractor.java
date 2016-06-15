package com.novoda.github.reports.service.network;

import java.util.Optional;

import retrofit2.Response;

public class LastPageExtractor {

    private static final String LAST = "last";

    private final PageExtractor pageExtractor;

    public static LastPageExtractor newInstance() {
        return new LastPageExtractor(new PageExtractor(LAST));
    }

    LastPageExtractor(PageExtractor pageExtractor) {
        this.pageExtractor = pageExtractor;
    }

    public Optional<Integer> getLastPageFrom(Response response) {
        return pageExtractor.getPage(response);
    }

}
