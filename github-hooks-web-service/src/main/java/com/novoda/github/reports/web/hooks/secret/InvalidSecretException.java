package com.novoda.github.reports.web.hooks.secret;

public class InvalidSecretException extends Exception {
    InvalidSecretException(String message) {
        super(message);
    }

    InvalidSecretException(Throwable cause) {
        super(cause);
    }
}
