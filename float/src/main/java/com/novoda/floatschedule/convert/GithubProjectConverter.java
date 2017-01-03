package com.novoda.floatschedule.convert;

import java.util.List;

public interface GithubProjectConverter {

    List<String> getFloatProjects(String repositoryName) throws NoMatchFoundException;

    List<String> getRepositories(String floatProject) throws NoMatchFoundException;

}
