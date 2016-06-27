package com.novoda.github.reports.floatschedule.convert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FloatGithubProjectConverterTest {

    private Map<String, List<String>> mapFromReader;

    @Mock
    JsonMapReader<Map<String, List<String>>> mockJsonMapReader;

    private FloatGithubProjectConverter floatGithubProjectConverter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        mapFromReader = new HashMap<>(1);
        mapFromReader.put("Float", Arrays.asList("repo1", "repo2"));
        when(mockJsonMapReader.readFromResource("projects.json")).thenReturn(mapFromReader);

        floatGithubProjectConverter = new FloatGithubProjectConverter(mockJsonMapReader);
    }

    @Test
    public void givenAJsonFileWithProjects_whenGettingTheFloatProjectNameForARepository_thenReturnsMatch() throws Exception {

        String actual = floatGithubProjectConverter.getFloatProject("repo2");

        assertThat("float", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenAJsonFileWithNoMatch_whenGettingTheFloatProjectNameForARepository_thenThrowsException() throws Exception {

        floatGithubProjectConverter.getFloatProject("repo420");
    }

    @Test
    public void givenAJsonFileWithProjects_whenGettingRepositoriesForTheFloatProject_thenReturnsMatch() throws Exception {

        List<String> actual = floatGithubProjectConverter.getRepositories("Float");

        assertEquals(mapFromReader.get("Float"), actual);
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenAJsonFileWithNoMappings_whenGettingRepositoriesForTheFloatProject_thenThrowsException() throws Exception {

        floatGithubProjectConverter.getRepositories("dab");
    }
}
