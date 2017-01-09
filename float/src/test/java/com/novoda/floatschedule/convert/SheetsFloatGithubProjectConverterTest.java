package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.ProjectSheetsServiceClient;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SheetsFloatGithubProjectConverterTest {

    private static final String ANY_REPOSITORY_NAME = "github-reports";
    private static final String ANOTHER_REPOSITORY_NAME = "project-d";
    private static final String ANY_PROJECT_NAME = "R & D";
    private static final String ANOTHER_PROJECT_NAME = "The Times - Scheduled";
    private static final Entry AN_ENTRY = new Entry(ANY_PROJECT_NAME, ANY_REPOSITORY_NAME + ", " + ANOTHER_REPOSITORY_NAME);
    private static final Entry ANOTHER_ENTRY = new Entry(ANOTHER_PROJECT_NAME, ANOTHER_REPOSITORY_NAME);

    @Mock
    ProjectSheetsServiceClient mockProjectSheetsServiceClient;
    @InjectMocks
    SheetsFloatGithubProjectConverter sheetsFloatGithubProjectConverter;

    @Test
    public void givenProjectToRepositoryNamesIsEmpty_whenGettingFloatProjects_thenTheApiIsHit() throws Exception {
        givenServiceIsSetup();

        sheetsFloatGithubProjectConverter.getFloatProjects(ANY_REPOSITORY_NAME);

        verify(mockProjectSheetsServiceClient).getProjectEntries();
    }

    @Test
    public void givenProjectToRepositoryNamesIsNotEmpty_whenGettingFloatProjects_thenTheApiIsNotHit() throws Exception {
        givenApiHasAlreadyBeenHit();

        sheetsFloatGithubProjectConverter.getFloatProjects(ANY_REPOSITORY_NAME);

        verify(mockProjectSheetsServiceClient, never()).getProjectEntries();
    }

    @Test
    public void givenProjectToRepositoryNamesIsEmpty_whenGettingRepositories_thenTheApiIsHit() {
        givenServiceIsSetup();

        sheetsFloatGithubProjectConverter.getRepositories(ANY_PROJECT_NAME);

        verify(mockProjectSheetsServiceClient).getProjectEntries();
    }

    @Test
    public void givenProjectToRepositoryNamesIsNotEmpty_whenGettingRepositories_thenTheApiIsNotHit() {
        givenApiHasAlreadyBeenHit();

        sheetsFloatGithubProjectConverter.getRepositories(ANY_PROJECT_NAME);

        verify(mockProjectSheetsServiceClient, never()).getProjectEntries();
    }

    @Test
    public void givenProjectEntries_whenGettingRepositoriesForAProject_thenTheyAreReturned() {
        givenApiHasAlreadyBeenHit();

        List<String> repositories = sheetsFloatGithubProjectConverter.getRepositories(ANY_PROJECT_NAME);

        assertThat(repositories).containsOnly(ANY_REPOSITORY_NAME, ANOTHER_REPOSITORY_NAME);
    }

    @Test
    public void givenProjectEntries_whenGettingFloatProjectForRepoInOneProject_thenItIsReturned() {
        givenApiHasAlreadyBeenHit();

        List<String> floatProjects = sheetsFloatGithubProjectConverter.getFloatProjects(ANY_REPOSITORY_NAME);

        assertThat(floatProjects).containsOnly(ANY_PROJECT_NAME.toLowerCase(Locale.UK));
    }

    @Test
    public void givenProjectEntries_whenGettingFloatProjectsForRepoInMultipleProjects_thenTheyAreReturned() {
        givenApiHasAlreadyBeenHit();

        List<String> floatProjects = sheetsFloatGithubProjectConverter.getFloatProjects(ANY_REPOSITORY_NAME);

        assertThat(floatProjects).containsOnly(ANY_PROJECT_NAME.toLowerCase(Locale.UK));
    }

    private void givenServiceIsSetup() {
        given(mockProjectSheetsServiceClient.getProjectEntries()).willReturn(Observable.just(AN_ENTRY));
    }

    private void givenApiHasAlreadyBeenHit() {
        given(mockProjectSheetsServiceClient.getProjectEntries()).willReturn(
                Observable.just(AN_ENTRY, ANOTHER_ENTRY)
        );
        sheetsFloatGithubProjectConverter.getFloatProjects(ANY_REPOSITORY_NAME);
        reset(mockProjectSheetsServiceClient);
    }
}
