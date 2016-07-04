package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FloatGithubProjectConverter {

    private final ProjectsReader projectsReader;

    public static FloatGithubProjectConverter newInstance() {
        return new FloatGithubProjectConverter(ProjectsReader.newInstance());
    }

    private FloatGithubProjectConverter(ProjectsReader projectsReader) {
        this.projectsReader = projectsReader;
    }

    public List<String> getFloatProjects(String repositoryName) throws IOException, NoMatchFoundException {
        readIfNeeded();

        List<String> projects = projectsReader.getContent().entrySet()
                .stream()
                .filter(entry -> repositoryIsInRepositoriesOfProject(repositoryName, entry))
                .map(Map.Entry::getKey)
                .distinct()
                .filter(notNull())
                .collect(Collectors.toList());

        if (projects.isEmpty()) {
            throw new NoMatchFoundException(repositoryName);
        }

        return projects;
    }

    private boolean repositoryIsInRepositoriesOfProject(String githubRepositoryName, Map.Entry<String, List<String>> projectWithRepositoryNames) {
        return projectWithRepositoryNames.getValue().stream()
                .filter(byRepositoriesContainingRepositoryWithName(githubRepositoryName))
                .count() > 0;
    }

    private Predicate<String> byRepositoriesContainingRepositoryWithName(String githubRepositoryName) {
        return githubProjects -> githubProjects.toLowerCase(Locale.UK).contains(githubRepositoryName.toLowerCase(Locale.UK));
    }

    private Predicate<String> notNull() {
        return projectName -> projectName != null;
    }

    public List<String> getRepositories(String floatProject) throws IOException, NoMatchFoundException {
        readIfNeeded();
        return projectsReader.getContent().entrySet()
                .stream()
                .filter(byProjectHavingRepositories(floatProject))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow((Supplier<RuntimeException>) () -> new NoMatchFoundException(floatProject));
    }

    private void readIfNeeded() throws IOException {
        if (projectsReader.hasContent()) {
            return;
        }
        projectsReader.read();
    }

    private Predicate<Map.Entry<String, List<String>>> byProjectHavingRepositories(String floatProject) {
        return entry -> floatProject.toLowerCase(Locale.UK).contains(entry.getKey().toLowerCase(Locale.UK));
    }
}
