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
import org.mockito.internal.verification.VerificationModeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
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
    public void givenProjectsWereRead_whenGettingTheFloatProjectNameForARepository_thenReturnsMatch() throws Exception {

        String actual = floatGithubProjectConverter.getFloatProject("repo2");

        assertThat("float", IsEqualIgnoringCase.equalToIgnoringCase(actual));
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenWereReadButThereIsNoMatch_whenGettingTheFloatProjectNameForARepository_thenThrowsException() throws Exception {

        floatGithubProjectConverter.getFloatProject("repo420");
    }

    @Test
    public void givenProjectsWereRead_whenGettingRepositoriesForTheFloatProject_thenReturnsMatch() throws Exception {

        List<String> actual = floatGithubProjectConverter.getRepositories("Float");

        assertEquals(projectToRepositories.get("Float"), actual);
    }

    @Test(expected = NoMatchFoundException.class)
    public void givenProjectsWereReadButThereIsNoMatch_whenGettingRepositoriesForTheFloatProject_thenThrowsException() throws Exception {

        floatGithubProjectConverter.getRepositories("dab");
    }

    @Test
    public void givenProjectsWereNotRead_whenGettingTheFloatProjectName_thenContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        floatGithubProjectConverter.getFloatProject("repo2");

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenProjectsWereAlreadyRead_whenGettingTheFloatProjectNameAgain_thenContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        floatGithubProjectConverter.getFloatProject("repo2");

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }

    @Test
    public void givenProjectsWereNotRead_whenGettingRepositoriesForTheFloatProject_thenContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        floatGithubProjectConverter.getRepositories("Float");

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenProjectsWereAlreadyRead_whenGettingRepositoriesForTheFloatProject_thenContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        floatGithubProjectConverter.getRepositories("Float");

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }
}
