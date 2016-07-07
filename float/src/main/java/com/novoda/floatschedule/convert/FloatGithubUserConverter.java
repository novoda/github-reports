package com.novoda.floatschedule.convert;

import com.novoda.github.reports.reader.UsersReader;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;
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
                .filter(byGithubUsername(githubUsername))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(noMatchFoundException(githubUsername));
    }

    private void readIfNeeded() throws IOException {
        if (usersReader.hasContent()) {
            return;
        }
        usersReader.read();
    }

    private Predicate<Map.Entry<String, String>> byGithubUsername(String githubUsername) {
        return entry -> entry.getValue().equalsIgnoreCase(githubUsername);
    }

    private Supplier<RuntimeException> noMatchFoundException(String username) {
        return () -> new NoMatchFoundException(username);
    }

    public String getGithubUser(String floatName) throws IOException, NoMatchFoundException {
        readIfNeeded();
        return usersReader.getContent().entrySet()
                .stream()
                .filter(byFloatUsername(floatName))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(noMatchFoundException(floatName));
    }

    private Predicate<Map.Entry<String, String>> byFloatUsername(String floatName) {
        return entry -> entry.getKey().equalsIgnoreCase(floatName);
    }
}
