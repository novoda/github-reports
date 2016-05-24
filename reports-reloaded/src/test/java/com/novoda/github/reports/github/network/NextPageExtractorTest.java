package com.novoda.github.reports.github.network;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import okhttp3.Headers;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

public class NextPageExtractorTest {

    private static final int ANY_NEXT_PAGE = 88;
    private static final String ANY_LINK_WITH_NEXT_PAGE = "<https://api.github.com/search/code?page=" + ANY_NEXT_PAGE + ">; rel=\"next\"";
    private static final String ANY_LINK_WITHOUT_NEXT_PAGE = "<https://api.github.com/search/code?page=34>; rel=\"last\"";

    private static final String ANY_BODY = "corpo";
    private static final Headers ANY_HEADERS_WITHOUT_LINK = new Headers.Builder().build();
    private static final Headers ANY_HEADERS_WITHOUT_NEXT_PAGE = new Headers.Builder().add("Link", ANY_LINK_WITHOUT_NEXT_PAGE).build();
    private static final Headers ANY_HEADERS_WITH_NEXT_PAGE = new Headers.Builder().add("Link", ANY_LINK_WITH_NEXT_PAGE).build();

    private Response<String> response;
    private NextPageExtractor nextPageExtractor;

    @Before
    public void setUp() throws Exception {
        nextPageExtractor = new NextPageExtractor();
    }

    @Test
    public void givenAResponseWithoutTheLinkHeader_whenGettingTheNextPage_weGetAnEmptyResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITHOUT_LINK);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void givenAResponseWithoutTheNextPageLink_whenGettingTheNextPage_weGetAnEmptyResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITHOUT_NEXT_PAGE);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.empty(), actual);
    }

    @Test
    public void givenAResponseWithTheNextPageLink_whenGettingTheNextPage_weGetTheNextPageResult() throws Exception {
        response = Response.success(ANY_BODY, ANY_HEADERS_WITH_NEXT_PAGE);

        Optional<Integer> actual = nextPageExtractor.getNextPageFrom(response);

        assertEquals(Optional.of(ANY_NEXT_PAGE), actual);
    }
}
