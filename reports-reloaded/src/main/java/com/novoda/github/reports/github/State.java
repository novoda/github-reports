package com.novoda.github.reports.github;

public enum State {

    OPEN("open"),
    CLOSED("closed");
    //ALL("all");

    private final String value;

    State(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
