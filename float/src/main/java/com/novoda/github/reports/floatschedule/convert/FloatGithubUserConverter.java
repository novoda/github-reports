package com.novoda.github.reports.floatschedule.convert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class FloatGithubUserConverter {

    private final JsonMapReader<Map<String, String>> jsonMapReader;
    private Map<String, String> floatToGithubUser;

    public static FloatGithubUserConverter newInstance() {
        JsonMapReader<Map<String, String>> jsonReader = JsonMapReader.newStringToStringInstance();
        return new FloatGithubUserConverter(jsonReader);
    }

    FloatGithubUserConverter(JsonMapReader<Map<String, String>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
    }

    public String getFloatUser(String githubUsername) throws IOException, UserNotFoundException {
        readIfNeeded();

        final String[] match = { null };
        floatToGithubUser.forEach((floatName, githubName) -> {
            if (githubName.equalsIgnoreCase(githubUsername)) {
                match[0] = floatName;
            }
        });

        if (match[0] == null) {
            throw new UserNotFoundException(githubUsername);
        }

        return match[0];
    }

    private boolean fileContentsAlreadyRead() {
        return floatToGithubUser != null;
    }

    public String getGithubUser(String floatName) throws IOException, UserNotFoundException {
        readIfNeeded();
        final String[] match = { null };
        floatToGithubUser.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(floatName))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getValue());

        if (match[0] == null) {
            throw new UserNotFoundException(floatName);
        }

        return match[0];
    }

    private void readIfNeeded() throws IOException {
        if (fileContentsAlreadyRead()) {
            return;
        }
        try {
            floatToGithubUser = jsonMapReader.readFromResource("users.json");
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file.");
        }
    }

    public static class UserNotFoundException extends Exception {

        UserNotFoundException(String username) {
            super("Could not find a match for user \"" + username + "\". Please check your mappings file and/or your query string.");
        }
    }
}
