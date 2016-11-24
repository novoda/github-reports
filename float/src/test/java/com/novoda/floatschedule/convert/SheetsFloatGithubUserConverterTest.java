package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.SheetsServiceClient;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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

        assertThat("float pirata", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenUsersWereReadButThereIsNoMatch_whenGettingTheFloatUsernameForAGithubUsername_thenThrowsException() throws Exception {

        sheetsFloatGithubUserConverter.getFloatUser("sebastião");
    }

    @Test
    public void givenUsersWereRead_whenGettingTheGithubUsernameForAFloatUsername_thenReturnsMatch() throws Exception {

        String actual = sheetsFloatGithubUserConverter.getGithubUser("float pirata");

        assertThat("github meirinho", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenUsersWereReadButThereIsNoMatch_whenGettingTheGithubUsernameForAFloatUsername_thenThrowsException() throws Exception {

        sheetsFloatGithubUserConverter.getGithubUser("palerma");
    }

    @Test
    public void givenUsersWereNotRead_whenGettingTheGithubUsername_thenContentIsRead() throws Exception {
        given(mockSheetsServiceClient.getEntries()).willReturn(Observable.from(Collections.emptyList()));

        sheetsFloatGithubUserConverter.getGithubUser("float pirata");

        verify(mockSheetsServiceClient).getEntries();
    }

    @Test
    public void givenUsersWereAlreadyRead_whenGettingTheGithubUsernameAgain_thenContentIsNotReadAgain() throws Exception {

        sheetsFloatGithubUserConverter.getGithubUser("float pirata");

        verify(mockSheetsServiceClient, VerificationModeFactory.times(0)).getEntries();
    }

    @Test
    public void givenUsersWereNotRead_whenGettingTheFloatUsername_thenContentIsRead() throws Exception {
        given(mockSheetsServiceClient.getEntries()).willReturn(Observable.from(Collections.emptyList()));

        sheetsFloatGithubUserConverter.getFloatUser("github meirinho");

        verify(mockSheetsServiceClient).getEntries();
    }

    @Test
    public void givenUsersWereAlreadyRead_whenGettingTheFloatUsernameAgain_thenContentIsNotReadAgain() throws Exception {

        sheetsFloatGithubUserConverter.getFloatUser("github meirinho");

        verify(mockSheetsServiceClient, VerificationModeFactory.times(0)).getEntries();
    }

    @Test
    public void givenUsersWhereRead_whenGettingGithubUsers_thenReturnAllUsers() throws Exception {

        List<String> actualGithubUsers = sheetsFloatGithubUserConverter.getGithubUsers();

        assertEquals(Arrays.asList("github meirinho", "sparrow"), actualGithubUsers);
    }

    @Test
    public void givenUsersWereReadAndHaveNoContent_whenGettingGithubUsers_thenReturnAllUsers() throws Exception {
        given(mockSheetsServiceClient.getEntries()).willReturn(Observable.from(Collections.emptyList()));

        List<String> actualGithubUsers = sheetsFloatGithubUserConverter.getGithubUsers();

        assertEquals(Collections.emptyList(), actualGithubUsers);
    }

    private List<Entry> givenEntries() {
        Entry anEntry = new Entry("Float Pirata", "github meirinho");
        Entry anotherEntry = new Entry("Jack Pirata", "sparrow");
        return Arrays.asList(anEntry, anotherEntry);
    }

}
