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
    private final Map<String, List<String>> projectsToRepositories;

    public static FloatGithubProjectConverter newInstance() {
        JsonMapReader<Map<String, List<String>>> jsonMapReader = JsonMapReader.newStringToListOfStringsInstance();
        return new FloatGithubProjectConverter(jsonMapReader);
    }

    FloatGithubProjectConverter(JsonMapReader<Map<String, List<String>>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
        projectsToRepositories = new HashMap<>();
    }

    @Nullable
    public String getFloatProject(String repositoryName) throws IOException {
        readIfNeeded();

        String lowerCaseRepository = repositoryName.toLowerCase(Locale.UK);

        final String[] match = { null };
        projectsToRepositories.forEach((floatProject, repositories) -> {
            if (repositories.contains(lowerCaseRepository)) {
                match[0] = floatProject;
            }
        });

        return match[0];
    }

    private void readIfNeeded() throws IOException {
        if (!projectsToRepositories.isEmpty()) {
            return;
        }
        try {
            projectsToRepositories.putAll(jsonMapReader.readFromResource("projects.json"));
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file.");
        }
    }

    @Nullable
    public List<String> getRepositories(String floatProject) throws IOException {
        readIfNeeded();
        return projectsToRepositories.get(floatProject.toLowerCase(Locale.UK));
    }
}
