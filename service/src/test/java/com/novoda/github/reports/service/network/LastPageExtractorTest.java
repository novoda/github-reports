package com.novoda.github.reports.service.network;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import okhttp3.Headers;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

public class LastPageExtractorTest {

    private static final int ANY_LAST_PAGE = 88;

    private static final String ANY_LINK_WITH_LAST_PAGE_AND_PER_PAGE =
            "<https://api.github.com/organizations/74874/repos?page=" + ANY_LAST_PAGE + "&per_page=100>; rel=\"last\"";
    private static final String ANY_LINK_WITH_LAST_PAGE_AND_OTHER_PARAMS =
            "<https://api.github.com/organizations/74874/repos?state=all&page=" + ANY_LAST_PAGE + "&creator=takecare&per_page=100>; rel=\"last\"";
    private static final String ANY_LINK_WITHOUT_LAST_PAGE = "<https://api.github.com/organizations/74874/repos?page=1>; rel=\"first\", " +
            "<https://api.github.com/organizations/74874/repos?page=4>; rel=\"prev\"";
    private static final String ANY_LINK_WITH_LAST_PAGE = "<https://api.github.com/organizations/74874/repos?page=2>; rel=\"next\", " +
            "<https://api.github.com/organizations/74874/repos?page=" + ANY_LAST_PAGE + ">; rel=\"last\"";
    private static final String ANY_LINK_WITH_LAST_PAGE_AND_OTHERS = "<https://api.github.com/organizations/74874/repos?page=4>; rel=\"next\", " +
            "<https://api.github.com/organizations/74874/repos?page=" + ANY_LAST_PAGE + ">; rel=\"last\", " +
            "<https://api.github.com/organizations/74874/repos?page=1>; rel=\"first\", " +
            "<https://api.github.com/organizations/74874/repos?page=2>; rel=\"prev\"";

    private static final String ANY_BODY = "corpo";
    private static final Headers ANY_HEADERS_WITHOUT_LINK = new Headers.Builder().build();
    private static final Headers ANY_HEADERS_WITHOUT_LAST_PAGE = new Headers.Builder().add("Link", ANY_LINK_WITHOUT_LAST_PAGE).build();
    private static final Headers ANY_HEADERS_WITH_LAST_PAGE = new Headers.Builder().add("Link", ANY_LINK_WITH_LAST_PAGE).build();
    private static final Headers ANY_HEADERS_WITH_LAST_PAGE_AND_PER_PAGE = new Headers.Builder()
            .add("Link", ANY_LINK_WITH_LAST_PAGE_AND_PER_PAGE)
            .build();
    private static final Headers ANY_HEADERS_WITH_NEXT_PAGE_AND_OTHER_PARAMS = new Headers.Builder()
            .add("Link", ANY_LINK_WITH_LAST_PAGE_AND_OTHER_PARAMS)
            .build();
    private static final Headers ANY_HEADERS_WITH_LAST_PAGE_AND_OTHERS = new Headers.Builder()
            .add("Link", ANY_LINK_WITH_LAST_PAGE_AND_OTHERS)
            .build();

    private Response<String> response;
    private LastPageExtractor lastPageExtractor;

    @Before
    public void setUp() throws Exception {
        PageExtractor pageExtractor = new PageExtractor("last");
        lastPageExtractor = new LastPageExtractor(pageExtractor);
    }

    @Test
    public void givenResponseWithoutTheLinkHeader_whenGettingTheLastPage_thenReturnsAnEmptyResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITHOUT_LINK);

        Optional<Integer> actual = lastPageExtractor.getLastPageFrom(response);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void givenResponseWithoutTheLastPageLink_whenGettingTheLastPage_thenReturnsAnEmptyResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITHOUT_LAST_PAGE);

        Optional<Integer> actual = lastPageExtractor.getLastPageFrom(response);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void givenResponseWithTheLastPageLink_whenGettingTheLastPage_thenReturnsTheLastPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_LAST_PAGE);

        Optional<Integer> actual = lastPageExtractor.getLastPageFrom(response);

        assertEquals(Optional.of(ANY_LAST_PAGE), actual);
    }

    @Test
    public void givenResponseWithTheLastPageLinkAndPerPageCount_whenGettingTheLastPage_thenReturnsTheLastPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_LAST_PAGE_AND_PER_PAGE);

        Optional<Integer> actual = lastPageExtractor.getLastPageFrom(response);

        assertEquals(Optional.of(ANY_LAST_PAGE), actual);
    }

    @Test
    public void givenResponseWithTheLastPageLinkAndOtherQueries_whenGettingTheNextPage_thenReturnsTheLastPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_NEXT_PAGE_AND_OTHER_PARAMS);

        Optional<Integer> actual = lastPageExtractor.getLastPageFrom(response);

        assertEquals(Optional.of(ANY_LAST_PAGE), actual);
    }

    @Test
    public void givenResponseWithTheLastPageLinkAndOtherPageRels_whenGettingTheLastPage_thenReturnsTheLastPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_LAST_PAGE_AND_OTHERS);

        Optional<Integer> actual = lastPageExtractor.getLastPageFrom(response);

        assertEquals(Optional.of(ANY_LAST_PAGE), actual);
    }
}
