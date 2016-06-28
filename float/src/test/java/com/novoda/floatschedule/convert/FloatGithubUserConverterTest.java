package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.UsersReader;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
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
        when(mockUsersReader.getContent()).thenReturn(floatToGithubUserMap);
    }

    @Test
    public void givenAJsonFileWithUsers_whenGettingTheFloatUsernameForAGithubUsername_thenReturnsMatch() throws Exception {

        String actual = floatGithubUserConverter.getFloatUser("github meirinho");

        assertThat("float pirata", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenAJsonFileWithNoMatch_whenGettingTheFloatUsernameForAGithubUsername_thenThrowsException() throws Exception {

        floatGithubUserConverter.getFloatUser("sebastião");
    }

    @Test
    public void givenAJsonFileWithUsers_whenGettingTheGithubUsernameForAFloatUsername_thenReturnsMatch() throws Exception {

        String actual = floatGithubUserConverter.getGithubUser("float pirata");

        assertThat("github meirinho", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenAJsonFileWithNoMatch_whenGettingTheGithubUsernameForAFloatUsername_thenThrowsException() throws Exception {

        floatGithubUserConverter.getGithubUser("palerma");
    }
}
