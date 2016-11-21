package com.novoda.github.reports.reader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class UsersReader {

    // FIXME we're leaking float into core

    private final JsonMapReader<Map<String, String>> jsonMapReader;
    private final Map<String, String> floatToGithubUser;

    public static UsersReader newInstance() {
        JsonMapReader<Map<String, String>> jsonReader = JsonMapReader.newStringToStringInstance();
        return new UsersReader(jsonReader);
    }

    UsersReader(JsonMapReader<Map<String, String>> jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
        this.floatToGithubUser = new HashMap<>();
    }

    public void read() throws IOException {
        try {
            floatToGithubUser.putAll(jsonMapReader.readFromResource("users.json"));
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file: " + e.getMessage());
        }
    }

    public Map<String, String> getContent() {
        return floatToGithubUser;
    }

    public boolean hasContent() {
        return !floatToGithubUser.isEmpty();
    }

}
