package com.novoda.github.reports.batch.worker;

public interface Logger {

    void log(String what);

    default void log(String template, Object... args) {
        String message = String.format(template, args);
        log(message);
    }

    default void log(Throwable t) {
        log(t.toString());
        log(t.getMessage());
    }

}
