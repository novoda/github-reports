package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.UsersReader;

import java.io.IOException;

public class FloatGithubUserConverter {

    private final UsersReader usersReader;

    public static FloatGithubUserConverter newInstance() {
        return new FloatGithubUserConverter(UsersReader.newInstance());
    }

    private FloatGithubUserConverter(UsersReader usersReader) {
        this.usersReader = usersReader;
    }

    public String getFloatUser(String githubUsername) throws IOException, NoMatchFoundException {
        readIfNeeded();

        final String[] match = { null };
        usersReader.getContent().forEach((floatName, githubName) -> {
            if (githubName.equalsIgnoreCase(githubUsername)) {
                match[0] = floatName;
            }
        });

        if (match[0] == null) {
            throw new NoMatchFoundException(githubUsername);
        }

        return match[0];
    }

    public String getGithubUser(String floatName) throws IOException, NoMatchFoundException {
        readIfNeeded();
        final String[] match = { null };
        usersReader.getContent().entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(floatName))
                .findFirst()
                .ifPresent(entry -> match[0] = entry.getValue());

        if (match[0] == null) {
            throw new NoMatchFoundException(floatName);
        }

        return match[0];
    }

    private void readIfNeeded() throws IOException {
        if (usersReader.hasContent()) {
            return;
        }
        usersReader.read();
    }
}
