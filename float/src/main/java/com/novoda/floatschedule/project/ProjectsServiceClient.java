package com.novoda.floatschedule.project;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import rx.Observable;

public class ProjectsServiceClient {

    private final ProjectsReader projectsReader;

    public static ProjectsServiceClient newInstance() {
        return new ProjectsServiceClient(ProjectsReader.newInstance());
    }

    private ProjectsServiceClient(ProjectsReader projectsReader) {
        this.projectsReader = projectsReader;
    }

    public Observable<String> getAllProjectNames() {
        try {
            readIfNeeded();
            return Observable.from(projectsReader.getContent().keySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Observable.empty();
    }

    public Observable<String> getAllGithubRepositoryNames(List<String> projectNames) {
        try {
            readIfNeeded();

            List<String> repositoryNames = projectNames.stream()
                    .flatMap(projectName -> projectsReader.getContent().get(projectName).stream())
                    .distinct()
                    .collect(Collectors.toList());

            return Observable.from(repositoryNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Observable.empty();
    }

    private void readIfNeeded() throws IOException {
        if (projectsReader.hasContent()) {
            return;
        }
        projectsReader.read();
    }
}
