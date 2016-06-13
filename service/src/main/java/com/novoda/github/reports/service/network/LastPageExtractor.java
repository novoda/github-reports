package com.novoda.github.reports.service.network;

import java.util.Optional;

import retrofit2.Response;

public class LastPageExtractor {

    private static final String LAST = "last";

    private final PageExtractor pageExtractor;

    public static LastPageExtractor newInstance() {
        return new LastPageExtractor(new PageExtractor());
    }

    private LastPageExtractor(PageExtractor pageExtractor) {
        this.pageExtractor = pageExtractor;
    }

    Optional<Integer> getLastPageFrom(Response response) {
        return pageExtractor.getPage(LAST, response);
    }

}
