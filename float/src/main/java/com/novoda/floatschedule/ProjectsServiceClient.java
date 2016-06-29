package com.novoda.floatschedule;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import rx.Observable;

class ProjectsServiceClient {

    private final ProjectsReader projectsReader;

    public static ProjectsServiceClient newInstance() {
        return new ProjectsServiceClient(ProjectsReader.newInstance());
    }

    private ProjectsServiceClient(ProjectsReader projectsReader) {
        this.projectsReader = projectsReader;
    }

    Observable<String> getAllFloatProjectNames() {
        try {
            readIfNeeded();
            return Observable.from(projectsReader.getContent().keySet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Observable.empty();
    }

    Observable<String> getAllGithubRepositoryNames(List<String> floatProjectNames) {
        try {
            readIfNeeded();

            List<String> repositoryNames = floatProjectNames.stream()
                    .flatMap(floatProjectName -> projectsReader.getContent().get(floatProjectName).stream())
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
