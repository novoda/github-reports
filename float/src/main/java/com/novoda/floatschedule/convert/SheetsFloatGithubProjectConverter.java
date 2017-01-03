package com.novoda.floatschedule.convert;

import com.novoda.github.reports.sheets.network.ProjectSheetsServiceClient;
import com.novoda.github.reports.sheets.sheet.Entry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
                .filter(entry -> entry.getValue().contains(repositoryName.toLowerCase(Locale.UK)))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (floatProjects.isEmpty()) {
            throw new NoMatchFoundException(repositoryName);
        }

        return floatProjects;
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
        List<String> repositories = projectToRepositories.get(floatProject.toLowerCase(Locale.UK));

        if (repositories == null || repositories.isEmpty()) {
            throw new NoMatchFoundException(floatProject);
        }

        return repositories;
    }

}
