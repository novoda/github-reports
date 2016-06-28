package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FloatGithubProjectConverterTest {

    private Map<String, List<String>> projectToRepositories;

    @Mock
    private ProjectsReader mockProjectsReader;

    @InjectMocks
    private FloatGithubProjectConverter floatGithubProjectConverter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        projectToRepositories = new HashMap<>(1);
        projectToRepositories.put("Float", Arrays.asList("repo1", "repo2"));
        when(mockProjectsReader.getContent()).thenReturn(projectToRepositories);
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

        assertEquals(projectToRepositories.get("Float"), actual);
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenAJsonFileWithNoMappings_whenGettingRepositoriesForTheFloatProject_thenThrowsException() throws Exception {

        floatGithubProjectConverter.getRepositories("dab");
    }
}
