package com.novoda.github.reports.reader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.*;

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
    public void givenThereIsContent_whenGettingAllTheProjectNames_thenTheyAreEmitted() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllProjectNames()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("empreendimento", "trabalho", "projecto");
    }

    @Test
    public void givenThereIsNoContent_whenGettingAllTheProjectNames_thenTheContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        projectsServiceClient.getAllProjectNames();

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenThereIsContent_whenGettingAllTheProjectNames_thenTheContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        projectsServiceClient.getAllProjectNames();

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }

    @Test
    public void givenThereIsContent_whenGettingRepositoryNamesForProjects_thenTheNamesAreEmitted() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllGithubRepositoryNames(Collections.singletonList("trabalho"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("faz de conta");
    }

    @Test
    public void givenThereIsContent_whenGettingRepositoryNamesForProjects_thenTheNamesAreEmittedWithoutDuplicates() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllGithubRepositoryNames(Arrays.asList("projecto", "empreendimento"))
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("fazer nenhum", "olhar para o tecto", "produzir zero");
    }

    @Test
    public void givenThereIsNoContent_whenGettingRepositoryNamesForProjects_thenTheContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        projectsServiceClient.getAllGithubRepositoryNames(Collections.singletonList("empreendimento"));

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenThereIsNoContent_whenGettingRepositoryNamesForProjects_thenTheContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        projectsServiceClient.getAllGithubRepositoryNames(Collections.singletonList("empreendimento"));

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }
}
