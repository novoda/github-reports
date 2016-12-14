package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.UsersReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.novoda.floatschedule.convert.FloatNameFilter.byFloatName;
import static com.novoda.floatschedule.convert.GithubUsernameFilter.byGithubUsername;
import static com.novoda.floatschedule.convert.NoMatchFoundException.noMatchFoundExceptionFor;

public class FloatGithubUserConverter implements GithubUserConverter {

    private final UsersReader usersReader;

    public static FloatGithubUserConverter newInstance() {
        return new FloatGithubUserConverter(UsersReader.newInstance());
    }

    private FloatGithubUserConverter(UsersReader usersReader) {
        this.usersReader = usersReader;
    }

    public List<String> getGithubUsers() throws FailedToLoadMappingsException {
        readIfNeeded();
        return usersReader.getContent()
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public String getFloatUser(String githubUsername) throws FailedToLoadMappingsException, NoMatchFoundException {
        readIfNeeded();
        return usersReader.getContent().entrySet()
                .stream()
                .filter(byGithubUsername(githubUsername))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(noMatchFoundExceptionFor(githubUsername));
    }

    private void readIfNeeded() throws FailedToLoadMappingsException {
        if (usersReader.hasContent()) {
            return;
        }
        try {
            usersReader.read();
        } catch (IOException exception) {
            throw new FailedToLoadMappingsException(exception);
        }
    }

    public String getGithubUser(String floatName) throws FailedToLoadMappingsException, NoMatchFoundException {
        readIfNeeded();
        return usersReader.getContent().entrySet()
                .stream()
                .filter(byFloatName(floatName))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(noMatchFoundExceptionFor(floatName));
    }

}
