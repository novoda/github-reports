package com.novoda.github.reports.floatschedule.convert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class FloatGithubProjectConverter {

    private final JsonMapReader<Map<String, List<String>>> jsonMapReader;
    private final Map<String, List<String>> projectToRepositories;

    public static FloatGithubProjectConverter newInstance() {
        JsonMapReader<Map<String, List<String>>> jsonMapReader = JsonMapReader.newStringToListOfStringsInstance();
        return new FloatGithubProjectConverter(jsonMapReader);
    }

    FloatGithubProjectConverter(JsonMapReader<Map<String, List<String>>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
        projectToRepositories = new HashMap<>();
    }

    @Nullable
    public String getFloatProject(String repositoryName) throws IOException {
        readIfNeeded();
        final String[] match = { null };
        projectToRepositories.entrySet()
                .stream()
                .filter(entry -> containsIgnoreCase(repositoryName, entry.getValue()))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getKey());
        return match[0];
    }

    private boolean containsIgnoreCase(String target, List<String> list) {
        return list.stream()
                .filter(target::equalsIgnoreCase)
                .count() > 0;
    }

    private void readIfNeeded() throws IOException {
        if (!projectToRepositories.isEmpty()) {
            return;
        }
        try {
            projectToRepositories.putAll(jsonMapReader.readFromResource("projects.json"));
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file.");
        }
    }

    @Nullable
    public List<String> getRepositories(String floatProject) throws IOException {
        readIfNeeded();
        return projectToRepositories.get(floatProject.toLowerCase(Locale.UK));
    }
}
