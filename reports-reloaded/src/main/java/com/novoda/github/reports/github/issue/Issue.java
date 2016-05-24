package com.novoda.github.reports.github.issue;

public class Issue {

    private long id;

    private int number;

    private String title;

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("%d{%s}", id, title);
    }
}
