package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FloatGithubProjectConverter implements GithubProjectConverter {

    private final ProjectsReader projectsReader;

    public static FloatGithubProjectConverter newInstance() {
        return new FloatGithubProjectConverter(ProjectsReader.newInstance());
    }

    private FloatGithubProjectConverter(ProjectsReader projectsReader) {
        this.projectsReader = projectsReader;
    }

    @Override
    public List<String> getFloatProjects(String repositoryName) throws NoMatchFoundException {
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
        return projectWithRepositoryNames.getValue()
                .stream()
                .filter(byRepositoriesContainingRepositoryWithName(githubRepositoryName))
                .count() > 0;
    }

    private Predicate<String> byRepositoriesContainingRepositoryWithName(String githubRepositoryName) {
        return githubRepository -> githubRepository.toLowerCase(Locale.UK).contains(githubRepositoryName.toLowerCase(Locale.UK));
    }

    private Predicate<String> notNull() {
        return Objects::nonNull;
    }

    @Override
    public List<String> getRepositories(String floatProject) throws FailedToLoadMappingsException, NoMatchFoundException {
        readIfNeeded();
        return projectsReader.getContent().entrySet()
                .stream()
                .filter(byProjectHavingRepositories(floatProject))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(noMatchFoundException(floatProject));
    }

    private void readIfNeeded() throws FailedToLoadMappingsException {
        if (projectsReader.hasContent()) {
            return;
        }
        try {
            projectsReader.read();
        } catch (IOException exception) {
            throw new FailedToLoadMappingsException(exception);
        }
    }

    private Predicate<Map.Entry<String, List<String>>> byProjectHavingRepositories(String floatProject) {
        return floatToRepositories -> floatProject.toLowerCase(Locale.UK).contains(floatToRepositories.getKey().toLowerCase(Locale.UK));
    }

    private Supplier<RuntimeException> noMatchFoundException(String floatProject) {
        return () -> new NoMatchFoundException(floatProject);
    }
}
