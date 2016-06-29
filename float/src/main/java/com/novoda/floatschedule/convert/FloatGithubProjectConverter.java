package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FloatGithubProjectConverter {

    private final ProjectsReader projectsReader;

    public static FloatGithubProjectConverter newInstance() {
        return new FloatGithubProjectConverter(ProjectsReader.newInstance());
    }

    private FloatGithubProjectConverter(ProjectsReader projectsReader) {
        this.projectsReader = projectsReader;
    }

    String getFloatProject(String repositoryName) throws IOException, NoMatchFoundException {
        readIfNeeded();

        return projectsReader.getContent().entrySet()
                .stream()
                .filter(entry -> repositoryIsInRepositoriesOfProject(repositoryName, entry))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow((Supplier<RuntimeException>) () -> new NoMatchFoundException(repositoryName));
    }

    private boolean repositoryIsInRepositoriesOfProject(String githubRepositoryName, Map.Entry<String, List<String>> projectWithRepositoryNames) {
        return projectWithRepositoryNames.getValue().stream()
                .filter(byRepositoriesContainingRepositoryWithName(githubRepositoryName))
                .count() > 0;
    }

    private Predicate<String> byRepositoriesContainingRepositoryWithName(String githubRepositoryName) {
        return githubProjects -> githubProjects.toLowerCase(Locale.UK).contains(githubRepositoryName.toLowerCase(Locale.UK));
    }

    public List<String> getRepositories(String floatProject) throws IOException, NoMatchFoundException {
        readIfNeeded();

        @SuppressWarnings("unchecked") // it's safe 'cause we're only using the array here, we know the types
        final List<String>[] match = new List[]{ null };
        projectsReader.getContent().entrySet()
                .stream()
                .filter(byProjectHavingRepositories(floatProject))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getValue());

        if (match[0] == null) {
            throw new NoMatchFoundException(floatProject);
        }

        return match[0];
    }

    private Predicate<Map.Entry<String, List<String>>> byProjectHavingRepositories(String floatProject) {
        return entry -> floatProject.toLowerCase(Locale.UK).contains(entry.getKey().toLowerCase(Locale.UK));
    }

    private void readIfNeeded() throws IOException {
        if (projectsReader.hasContent()) {
            return;
        }
        projectsReader.read();
    }

}
