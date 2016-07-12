package com.novoda.github.reports.reader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ProjectsReaderTest {

    @Mock
    private JsonMapReader<Map<String, List<String>>> mockJsonMapReader;

    private ProjectsReader projectsReader;

    @Before
    public void setUp() {
        initMocks(this);
        projectsReader = new ProjectsReader(mockJsonMapReader);
    }

    @Test
    public void givenJsonMapReadReadsContentFromFile_whenReadingProjects_thenTheProjectsAreRead() throws Exception {
        Map<String, List<String>> content = new HashMap<>(1);
        content.put("chave", Arrays.asList("pouco valor", "muito valor"));
        when(mockJsonMapReader.readFromResource("projects.json")).thenReturn(content);

        projectsReader.read();

        assertTrue(projectsReader.hasContent());
    }

}
