package com.novoda.github.reports.batch.github.network;


import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import okhttp3.Headers;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

public class NextPageExtractorTest {

    private static final int ANY_NEXT_PAGE = 88;
    private static final String ANY_LINK_WITH_NEXT_PAGE = "<https://api.github.com/search/code?page=" + ANY_NEXT_PAGE + ">; rel=\"next\"";
    private static final String ANY_LINK_WITH_NEXT_PAGE_AND_PER_PAGE =
            "<https://api.github.com/organizations/74874/repos?page=" + ANY_NEXT_PAGE +"&per_page=100>; rel=\"next\"";
    private static final String ANY_LINK_WITH_NEXT_PAGE_AND_OTHERS =
            "<https://api.github.com/organizations/74874/repos?state=all&page=" + ANY_NEXT_PAGE +"&creator=takecare&per_page=100>; rel=\"next\"";
    private static final String ANY_LINK_WITHOUT_NEXT_PAGE = "<https://api.github.com/search/code?page=34>; rel=\"last\"";

    private static final String ANY_BODY = "corpo";
    private static final Headers ANY_HEADERS_WITHOUT_LINK = new Headers.Builder().build();
    private static final Headers ANY_HEADERS_WITHOUT_NEXT_PAGE = new Headers.Builder().add("Link", ANY_LINK_WITHOUT_NEXT_PAGE).build();
    private static final Headers ANY_HEADERS_WITH_NEXT_PAGE = new Headers.Builder().add("Link", ANY_LINK_WITH_NEXT_PAGE).build();
    private static final Headers ANY_HEADERS_WITH_NEXT_PAGE_AND_PER_PAGE = new Headers.Builder()
            .add("Link", ANY_LINK_WITH_NEXT_PAGE_AND_PER_PAGE)
            .build();
    private static final Headers ANY_HEADERS_WITH_NEXT_PAGE_AND_OTHERS = new Headers.Builder()
            .add("Link", ANY_LINK_WITH_NEXT_PAGE_AND_OTHERS)
            .build();

    private Response<String> response;
    private NextPageExtractor nextPageExtractor;

    @Before
    public void setUp() throws Exception {
        nextPageExtractor = new NextPageExtractor();
    }

    @Test
    public void givenResponseWithoutTheLinkHeader_whenGettingTheNextPage_thenReturnsAnEmptyResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITHOUT_LINK);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void givenResponseWithoutTheNextPageLink_whenGettingTheNextPage_thenReturnsAnEmptyResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITHOUT_NEXT_PAGE);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void givenResponseWithTheNextPageLink_whenGettingTheNextPage_thenReturnsTheNextPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_NEXT_PAGE);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.of(ANY_NEXT_PAGE), actual);
    }

    @Test
    public void givenResponseWithTheNextPageLinkAndPerPageCount_whenGettingTheNextPage_thenReturnsTheNextPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_NEXT_PAGE_AND_PER_PAGE);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.of(ANY_NEXT_PAGE), actual);
    }

    @Test
    public void givenResponseWithTheNextPageLinkAndOtherQueries_whenGettingTheNextPage_thenReturnsTheNextPage() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_NEXT_PAGE_AND_OTHERS);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.of(ANY_NEXT_PAGE), actual);
    }
}
