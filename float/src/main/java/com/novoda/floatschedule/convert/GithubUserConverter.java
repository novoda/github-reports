package com.novoda.floatschedule.convert;

import java.util.List;

public interface GithubUserConverter {

    List<String> getGithubUsers();

    String getFloatUser(String githubUsername) throws NoMatchFoundException;

    String getGithubUser(String floatName) throws NoMatchFoundException;

}
