package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.SheetsServiceClient;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class SheetsFloatGithubUserConverterTest {

    @Mock
    SheetsServiceClient mockSheetsServiceClient;

    private SheetsFloatGithubUserConverter sheetsFloatGithubUserConverter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        given(mockSheetsServiceClient.getEntries()).willReturn(Observable.from(givenEntries()));
        sheetsFloatGithubUserConverter = new SheetsFloatGithubUserConverter(mockSheetsServiceClient, Schedulers.immediate());
    }

    @Test
    public void givenUsersWereRead_whenGettingTheFloatUsernameForAGithubUsername_thenReturnsMatch() throws Exception {

        String actual = sheetsFloatGithubUserConverter.getFloatUser("github meirinho");

        assertThat(actual).isEqualToIgnoringCase("float pirata");
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenThereIsNoMatch_whenGettingTheFloatUsernameForAGithubUsername_thenThrowsException() throws Exception {

        sheetsFloatGithubUserConverter.getFloatUser("sebastião");

    }

    @Test
    public void givenUsersWereRead_whenGettingTheGithubUsernameForAFloatUsername_thenReturnsMatch() throws Exception {

        String actual = sheetsFloatGithubUserConverter.getGithubUser("float pirata");

        assertThat(actual).isEqualToIgnoringCase("github meirinho");
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenUsersWereReadButThereIsNoMatch_whenGettingTheGithubUsernameForAFloatUsername_thenThrowsException() throws Exception {

        sheetsFloatGithubUserConverter.getGithubUser("palerma");

    }

    @Test
    public void givenUsersWhereRead_whenGettingGithubUsers_thenReturnAllUsers() throws Exception {

        List<String> actualGithubUsers = sheetsFloatGithubUserConverter.getGithubUsers();

        assertThat(actualGithubUsers).containsExactlyInAnyOrder("github meirinho", "sparrow");
    }

    @Test
    public void givenThereAreNoUsers_whenGettingGithubUsers_thenReturnAllUsers() throws Exception {
        given(mockSheetsServiceClient.getEntries()).willReturn(Observable.from(Collections.emptyList()));

        List<String> actualGithubUsers = sheetsFloatGithubUserConverter.getGithubUsers();

        assertThat(actualGithubUsers).isEmpty();
    }

    private List<Entry> givenEntries() {
        Entry anEntry = new Entry("Float Pirata", "github meirinho");
        Entry anotherEntry = new Entry("Jack Pirata", "sparrow");
        return Arrays.asList(anEntry, anotherEntry);
    }

}
