package com.novoda.github.reports.service.network;

import java.util.Optional;

import retrofit2.Response;

class NextPageExtractor {

    private static final String NEXT = "next";

    private final PageExtractor pageExtractor;

    public static NextPageExtractor newInstance() {
        return new NextPageExtractor(new PageExtractor());
    }

    private NextPageExtractor(PageExtractor pageExtractor) {
        this.pageExtractor = pageExtractor;
    }

    Optional<Integer> getNextPageFrom(Response response) {
        return pageExtractor.getPage(NEXT, response);
    }

}
