package com.novoda.contributions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.novoda.contributions.FloatToGitHubUsernameTest.ThrowableCapture.captureThrowable;
import static org.assertj.core.api.Assertions.assertThat;

public class FloatToGitHubUsernameTest {

    private final Map<String, String> table = new HashMap<>();

    @Test
    public void givenEntryIsFoundThenItIsReturned() throws Exception {
        table.put("FloatEntry", "GitHubResult");
        FloatToGitHubUsername lookup = new FloatToGitHubUsername(table);

        assertThat(lookup.lookup("FloatEntry"))
                .isEqualTo("GitHubResult");
    }

    @Test
    public void givenEntryIsNotFoundThenErrorIsThrown() throws Exception {
        table.clear();
        FloatToGitHubUsername lookup = new FloatToGitHubUsername(table);

        Throwable throwable = captureThrowable(lookup::lookup, "NotToBeFound");

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