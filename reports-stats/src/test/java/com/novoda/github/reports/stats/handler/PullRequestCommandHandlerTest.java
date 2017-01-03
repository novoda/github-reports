package com.novoda.github.reports.stats.handler;

import com.novoda.floatschedule.convert.SheetsFloatGithubUserConverter;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.stats.command.PullRequestOptions;
import com.novoda.github.reports.stats.command.PullRequestOptionsGroupBy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PullRequestCommandHandlerTest {

    private static final List<String> ANY_REPOSITORIES = Arrays.asList("some", "repo", "here");
    private static final List<String> NO_USERS = Collections.emptyList();
    private static final PullRequestOptionsGroupBy ANY_GROUP_BY = PullRequestOptionsGroupBy.MONTH;
    private static final Boolean ANY_WITH_AVERAGE = true;
    private static final Date ANY_FROM = new Date();
    private static final Date ANY_TO = new Date();
    private static final TimeZone ANY_TIMEZONE = TimeZone.getTimeZone("Europe/London");
    private static final List<String> ALL_USERS = Arrays.asList("all", "users", "in", "organisation", "here");

    @Mock
    private EventDataLayer mockDataLayer;

    @Mock
    SheetsFloatGithubUserConverter mockConverter;

    @InjectMocks
    private PullRequestCommandHandler handler;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void givenOptionsWithNoUsers_whenHandle_thenGetUsersFromConverter() throws DataLayerException, IOException {
        given(mockConverter.getGithubUsers()).willReturn(ALL_USERS);
        PullRequestOptions optionsWithNoUsers = givenPullRequestOptionsWith(NO_USERS);

        handler.handle(optionsWithNoUsers);

        verifyDataLayerWasCalledWith(ALL_USERS);
    }

    @Test
    public void givenOptionsWithUsers_whenHandle_thenDoNotRetrieveUsersFromConverter() throws DataLayerException, IOException {
        List<String> someUsers = Arrays.asList("few", "users");
        PullRequestOptions optionsWithUsers = givenPullRequestOptionsWith(someUsers);

        handler.handle(optionsWithUsers);

        verify(mockConverter, never()).getGithubUsers();
    }

    @Test
    public void givenOptionsWithUsers_whenHandle_thenGetStatsWithUsersFromOptions() throws DataLayerException {
        List<String> someUsers = Arrays.asList("few", "users");
        PullRequestOptions optionsWithUsers = givenPullRequestOptionsWith(someUsers);

        handler.handle(optionsWithUsers);

        verifyDataLayerWasCalledWith(someUsers);
    }

    private PullRequestOptions givenPullRequestOptionsWith(List<String> users) {
        return new PullRequestOptions(ANY_REPOSITORIES, users, ANY_GROUP_BY, ANY_WITH_AVERAGE, ANY_FROM, ANY_TO, ANY_TIMEZONE);
    }

    private void verifyDataLayerWasCalledWith(List<String> users) throws DataLayerException {
        verify(mockDataLayer).getOrganisationStats(
                ANY_FROM,
                ANY_TO,
                ANY_REPOSITORIES,
                users,
                EventDataLayer.PullRequestStatsGroupBy.MONTH,
                ANY_WITH_AVERAGE
        );
    }

}
