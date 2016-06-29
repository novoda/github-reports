package com.novoda.floatschedule;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProjectsServiceClientTest {

    @Mock
    private ProjectsReader mockProjectsReader;

    @InjectMocks
    private ProjectsServiceClient projectsServiceClient;

    @Before
    public void setUp() {
        initMocks(this);
        Map<String, List<String>> content = new HashMap<>(1);
        content.put("projecto", Arrays.asList("fazer nenhum", "olhar para o tecto"));
        content.put("trabalho", Collections.singletonList("faz de conta"));
        content.put("empreendimento", Arrays.asList("olhar para o tecto", "produzir zero"));
        when(mockProjectsReader.getContent()).thenReturn(content);
    }

    @Test
    public void givenThereIsContent_whenGettingAllTheFloatProjectNames_thenTheyAreEmitted() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllFloatProjectNames()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("empreendimento", "trabalho", "projecto");
    }

    @Test
    public void givenThereIsNoContent_whenGettingAllTheFloatProjectnames_thenTheContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        projectsServiceClient.getAllFloatProjectNames();

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenThereIsContent_whenGettingAllTheFloatProjectnames_thenTheContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        projectsServiceClient.getAllFloatProjectNames();

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }

    @Test
    public void givenThereIsContent_whenGettingRepositoryNamesForFloatProjects_thenTheNamesAreEmitted() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllGithubRepositoryNames(Collections.singletonList("trabalho"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("faz de conta");
    }

    @Test
    public void givenThereIsContent_whenGettingRepositoryNamesForFloatProjects_thenTheNamesAreEmittedWithoutDuplicates() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllGithubRepositoryNames(Arrays.asList("projecto", "empreendimento"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("fazer nenhum", "olhar para o tecto", "produzir zero");
    }

    @Test
    public void givenThereIsNoContent_whenGettingRepositoryNamesForFloatProjects_thenTheContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        projectsServiceClient.getAllGithubRepositoryNames(Collections.singletonList("empreendimento"));

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenThereIsNoContent_whenGettingRepositoryNamesForFloatProjects_thenTheContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        projectsServiceClient.getAllGithubRepositoryNames(Collections.singletonList("empreendimento"));

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }
}
