package com.novoda.floatschedule.convert;

import java.util.List;

public interface GithubUserConverter {

    // @RUI revise the use of 'Expression' here and borrow it down

    List<String> getGithubUsers() throws Exception;

    String getFloatUser(String githubUsername) throws Exception;

    String getGithubUser(String floatName) throws Exception;

}
