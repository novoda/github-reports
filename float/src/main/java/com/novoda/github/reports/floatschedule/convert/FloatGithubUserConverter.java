package com.novoda.github.reports.floatschedule.convert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class FloatGithubUserConverter {

    private final JsonMapReader jsonMapReader;
    private Map<String, String> floatToGithubUsers;

    FloatGithubUserConverter(JsonMapReader jsonMapReader) {
        this.jsonMapReader = jsonMapReader;
    }

    @Nullable
    public String getFloatUser(String githubUsername) throws IOException {
        readIfNeeded();
        final String[] match = { null };
        floatToGithubUsers.forEach((floatName, githubName) -> {
            if (githubName.equalsIgnoreCase(githubUsername)) {
                match[0] = floatName;
            }
        });
        return match[0];
    }

    private void readIfNeeded() throws IOException {
        if (!floatToGithubUsers.isEmpty()) {
            return;
        }
        try {
            floatToGithubUsers = jsonMapReader.readFromResource("users.json");
        } catch (URISyntaxException | IOException e) {
            throw new IOException("Could not read users from file.");
        }
    }

    @Nullable
    public String getGithubUser(String floatName) throws IOException {
        readIfNeeded();
        return floatToGithubUsers.get(floatName.toLowerCase(Locale.UK));
    }
}
