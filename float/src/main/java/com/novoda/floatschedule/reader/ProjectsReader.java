package com.novoda.floatschedule.reader;

import com.novoda.floatschedule.convert.JsonMapReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectsReader {

    private final JsonMapReader<Map<String, List<String>>> jsonMapReader;
    private final Map<String, List<String>> projectToRepositories;

    public static ProjectsReader newInstance() {
        JsonMapReader<Map<String, List<String>>> jsonMapReader = JsonMapReader.newStringToListOfStringsInstance();
        return new ProjectsReader(jsonMapReader);
    }

    ProjectsReader(JsonMapReader<Map<String, List<String>>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
        projectToRepositories = new HashMap<>();
    }

    public void read() throws IOException {
        try {
            projectToRepositories.putAll(jsonMapReader.readFromResource("projects.json"));
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read projects from file: " + e.getMessage());
        }
    }

    public Map<String, List<String>> getContent() {
        return projectToRepositories;
    }

    public boolean hasContent() {
        return !projectToRepositories.isEmpty();
    }
}
