package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.ProjectSheetsServiceClient;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import rx.schedulers.Schedulers;

public class SheetsFloatGithubProjectConverter {

    private final Map<String, List<String>> projectToRepositories;
    private final ProjectSheetsServiceClient projectSheetsServiceClient;

    public static SheetsFloatGithubProjectConverter newInstance() {
        ProjectSheetsServiceClient projectSheetsServiceClient = ProjectSheetsServiceClient.newInstance();
        return new SheetsFloatGithubProjectConverter(projectSheetsServiceClient);
    }

    SheetsFloatGithubProjectConverter(ProjectSheetsServiceClient projectSheetsServiceClient) {
        projectToRepositories = new HashMap<>();
        this.projectSheetsServiceClient = projectSheetsServiceClient;
    }

    public List<String> getFloatProjects(String repositoryName) throws NoMatchFoundException {
        readIfNeeded();
        List<String> floatProjects = projectToRepositories.entrySet().stream()
                .filter(byRepository(repositoryName))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (floatProjects.isEmpty()) {
            throw new NoMatchFoundException(repositoryName);
        }

        return floatProjects;
    }

    private Predicate<Map.Entry<String, List<String>>> byRepository(String name) {
        return entry -> entry.getValue().contains(name.toLowerCase(Locale.UK));
    }

    private void readIfNeeded() {
        if (!projectToRepositories.isEmpty()) {
            return;
        }
        projectSheetsServiceClient.getProjectEntries()
                .subscribeOn(Schedulers.immediate())
                .subscribe(this::addToProjectToRepositories);
    }

    private void addToProjectToRepositories(Entry entry) {
        String repositoryNames[] = entry.getContent().split(",");
        List<String> lowerCaseRepositoryNames = Arrays.stream(repositoryNames)
                .map(repositoryName -> repositoryName.trim().toLowerCase(Locale.UK))
                .collect(Collectors.toList());
        projectToRepositories.put(entry.getTitle().toLowerCase(Locale.UK), lowerCaseRepositoryNames);
    }

    public List<String> getRepositories(String floatProject) throws NoMatchFoundException {
        readIfNeeded();

        List<String> repositories = projectToRepositories.entrySet().stream()
                .filter(byFloatProjectName(floatProject))
                .findFirst()
                .orElseThrow(NoMatchFoundException.noMatchFoundExceptionFor(floatProject))
                .getValue();

        if (repositories.isEmpty()) {
            throw new NoMatchFoundException(floatProject);
        }

        return repositories;
    }

    private Predicate<Map.Entry<String, List<String>>> byFloatProjectName(String floatProject) {
        return entry -> floatProject.toLowerCase(Locale.UK).contains(entry.getKey());
    }

}
