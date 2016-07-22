package com.novoda.floatschedule.convert;

import com.novoda.github.reports.reader.UsersReader;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FloatGithubUserConverterTest {

    @Mock
    private UsersReader mockUsersReader;

    @InjectMocks
    private FloatGithubUserConverter floatGithubUserConverter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        Map<String, String> floatToGithubUserMap = new HashMap<>(1);
        floatToGithubUserMap.put("Float Pirata", "github meirinho");
        floatToGithubUserMap.put("Jack Pirata", "sparrow");
        given(mockUsersReader.getContent()).willReturn(floatToGithubUserMap);
    }

    @Test
    public void givenUsersWereRead_whenGettingTheFloatUsernameForAGithubUsername_thenReturnsMatch() throws Exception {

        String actual = floatGithubUserConverter.getFloatUser("github meirinho");

        assertThat("float pirata", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenUsersWereReadButThereIsNoMatch_whenGettingTheFloatUsernameForAGithubUsername_thenThrowsException() throws Exception {

        floatGithubUserConverter.getFloatUser("sebastião");
    }

    @Test
    public void givenUsersWereRead_whenGettingTheGithubUsernameForAFloatUsername_thenReturnsMatch() throws Exception {

        String actual = floatGithubUserConverter.getGithubUser("float pirata");

        assertThat("github meirinho", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenUsersWereReadButThereIsNoMatch_whenGettingTheGithubUsernameForAFloatUsername_thenThrowsException() throws Exception {

        floatGithubUserConverter.getGithubUser("palerma");
    }

    @Test
    public void givenUsersWereNotRead_whenGettingTheGithubUsername_thenContentIsRead() throws Exception {
        given(mockUsersReader.hasContent()).willReturn(false);

        floatGithubUserConverter.getGithubUser("float pirata");

        verify(mockUsersReader).read();
    }

    @Test
    public void givenUsersWereAlreadyRead_whenGettingTheGithubUsernameAgain_thenContentIsNotReadAgain() throws Exception {
        given(mockUsersReader.hasContent()).willReturn(true);

        floatGithubUserConverter.getGithubUser("float pirata");

        verify(mockUsersReader, VerificationModeFactory.times(0)).read();
    }

    @Test
    public void givenUsersWereNotRead_whenGettingTheFloatUsername_thenContentIsRead() throws Exception {
        given(mockUsersReader.hasContent()).willReturn(false);

        floatGithubUserConverter.getFloatUser("github meirinho");

        verify(mockUsersReader).read();
    }

    @Test
    public void givenUsersWereAlreadyRead_whenGettingTheFloatUsernameAgain_thenContentIsNotReadAgain() throws Exception {
        given(mockUsersReader.hasContent()).willReturn(true);

        floatGithubUserConverter.getFloatUser("github meirinho");

        verify(mockUsersReader, VerificationModeFactory.times(0)).read();
    }
    
    @Test
    public void givenUsersWhereRead_whenGettingGithubUsers_thenReturnAllUsers() throws IOException {
        given(mockUsersReader.hasContent()).willReturn(true);

        List<String> actualGithubUsers = floatGithubUserConverter.getGithubUsers();

        assertEquals(Arrays.asList("github meirinho", "sparrow"), actualGithubUsers);
    }

    @Test
    public void givenUsersWereReadAndHaveNoContent_whenGettingGithubUsers_thenReturnAllUsers() throws IOException {
        given(mockUsersReader.hasContent()).willReturn(true);
        given(mockUsersReader.getContent()).willReturn(Collections.emptyMap());

        List<String> actualGithubUsers = floatGithubUserConverter.getGithubUsers();

        assertEquals(Collections.emptyList(), actualGithubUsers);
    }
}
