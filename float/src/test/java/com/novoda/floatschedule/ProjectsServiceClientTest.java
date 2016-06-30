package com.novoda.floatschedule;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.util.Arrays;
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
        content.put("trabalho", Arrays.asList("faz de conta"));
        when(mockProjectsReader.getContent()).thenReturn(content);
    }

    @Test
    public void givenFileWithProjectsAndRepositories_whenGettingAllTheFloatProjectNames_thenTheyAreEmitted() throws Exception {

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        projectsServiceClient.getAllFloatProjectNames()
                .subscribeOn(Schedulers.immediate())
                .subscribe(testSubscriber);

        testSubscriber.assertValues("trabalho", "projecto");
    }

    @Test
    public void givenThereIsNoContent_whenGettingAllTheFloatProjectNames_thenTheContentIsRead() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(false);

        projectsServiceClient.getAllFloatProjectNames();

        verify(mockProjectsReader).read();
    }

    @Test
    public void givenThereIsContent_whenGettingAllTheFloatProjectNames_thenTheContentIsNotReadAgain() throws Exception {
        when(mockProjectsReader.hasContent()).thenReturn(true);

        projectsServiceClient.getAllFloatProjectNames();

        verify(mockProjectsReader, VerificationModeFactory.times(0)).read();
    }
}
