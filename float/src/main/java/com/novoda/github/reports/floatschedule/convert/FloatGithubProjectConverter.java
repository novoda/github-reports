package com.novoda.github.reports.floatschedule.convert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class FloatGithubProjectConverter {

    private final JsonMapReader<Map<String, List<String>>> jsonMapReader;
    private Map<String, List<String>> projectToRepositories;

    public static FloatGithubProjectConverter newInstance() {
        JsonMapReader<Map<String, List<String>>> jsonMapReader = JsonMapReader.newStringToListOfStringsInstance();
        return new FloatGithubProjectConverter(jsonMapReader);
    }

    FloatGithubProjectConverter(JsonMapReader<Map<String, List<String>>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
    }

    public String getFloatProject(String repositoryName) throws IOException, NoMatchFoundException {
        readIfNeeded();

        final String[] match = { null };
        projectToRepositories.entrySet()
                .stream()
                .filter(entry -> containsIgnoreCase(repositoryName, entry.getValue()))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getKey());

        if (match[0] == null) {
            throw new NoMatchFoundException(repositoryName);
        }

        return match[0];
    }

    private boolean fileContentsAlreadyRead() {
        return projectToRepositories != null;
    }

    private boolean containsIgnoreCase(String target, List<String> list) {
        return list.stream()
                .filter(target::equalsIgnoreCase)
                .count() > 0;
    }

    public List<String> getRepositories(String floatProject) throws IOException, NoMatchFoundException {
        readIfNeeded();

        @SuppressWarnings("unchecked") // it's safe 'cause we're only using the array here, where we we for sure the types
        final List<String>[] match = new List[]{ null };
        projectToRepositories.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(floatProject))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getValue());

        if (match[0] == null) {
            throw new NoMatchFoundException(floatProject);
        }

        return match[0];
    }

    private void readIfNeeded() throws IOException {
        if (fileContentsAlreadyRead()) {
            return;
        }
        try {
            projectToRepositories = jsonMapReader.readFromResource("projects.json");
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file.");
        }
    }

}
