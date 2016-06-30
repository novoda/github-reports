package com.novoda.floatschedule.convert;

import com.novoda.floatschedule.reader.UsersReader;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

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

        return usersReader.getContent().entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(githubUsername))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow((Supplier<RuntimeException>) () -> new NoMatchFoundException(githubUsername));
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
