package com.novoda.github.reports.floatschedule.convert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FloatGithubUserConverter {

    private final JsonMapReader<Map<String, String>> jsonMapReader;
    private final Map<String, String> floatToGithubUser;

    public static FloatGithubUserConverter newInstance() {
        JsonMapReader<Map<String, String>> jsonReader = JsonMapReader.newStringToStringInstance();
        return new FloatGithubUserConverter(jsonReader);
    }

    FloatGithubUserConverter(JsonMapReader<Map<String, String>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
        floatToGithubUser = new HashMap<>();
    }

    public String getFloatUserOrNull(String githubUsername) throws IOException {
        readIfNeeded();

        final String[] match = { null };
        floatToGithubUser.forEach((floatName, githubName) -> {
            if (githubName.equalsIgnoreCase(githubUsername)) {
                match[0] = floatName;
            }
        });
        return match[0];
    }

    private void readIfNeeded() throws IOException {
        if (!floatToGithubUser.isEmpty()) {
            return;
        }
        try {
            floatToGithubUser.putAll(jsonMapReader.readFromResource("users.json"));
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file.");
        }
    }

    public String getGithubUserOrNull(String floatName) throws IOException {
        readIfNeeded();
        final String[] match = { null };
        floatToGithubUser.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(floatName))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getValue());
        return match[0];
    }
}
