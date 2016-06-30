package com.novoda.floatschedule;

import com.novoda.floatschedule.reader.ProjectsReader;

import java.io.IOException;

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

    private void readIfNeeded() throws IOException {
        if (projectsReader.hasContent()) {
            return;
        }
        projectsReader.read();
    }
}
