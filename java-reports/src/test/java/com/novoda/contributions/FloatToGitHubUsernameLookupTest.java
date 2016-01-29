package com.novoda.contributions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.novoda.contributions.FloatToGitHubUsernameLookupTest.ThrowableCapture.captureThrowable;
import static org.assertj.core.api.Assertions.assertThat;

public class FloatToGitHubUsernameLookupTest {

    private final Map<String, String> table = new HashMap<>();

    @Test
    public void givenEntryIsFoundThenItIsReturned() throws Exception {
        table.put("FloatEntry", "GitHubResult");
        FloatToGitHubUsernameLookup lookup = new FloatToGitHubUsernameLookup(table);

        assertThat(lookup.getGitHubUsernameFor("FloatEntry"))
                .isEqualTo("GitHubResult");
    }

    @Test
    public void givenEntryIsNotFoundThenErrorIsThrown() throws Exception {
        table.clear();
        FloatToGitHubUsernameLookup lookup = new FloatToGitHubUsernameLookup(table);

        Throwable throwable = captureThrowable(lookup::getGitHubUsernameFor, "NotToBeFound");

        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageEndingWith("NotToBeFound");
    }

    public static class ThrowableCapture {

        public static Throwable captureThrowable(Function<String, String> function, String param) {
            try {
                function.apply(param);
            } catch (Throwable throwable) {
                return throwable;
            }
            return null;
        }
    }
}