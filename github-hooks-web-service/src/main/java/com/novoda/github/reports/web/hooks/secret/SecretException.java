package com.novoda.github.reports.web.hooks.secret;

public class SecretException extends Exception {
    SecretException(String message) {
        super(message);
    }

    SecretException(Throwable cause) {
        super(cause);
    }
}
