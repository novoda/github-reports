package com.novoda.github.reports.sheets.sheet;

public class Entry {

    private final String title;
    private final String content;

    public Entry(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Entry{title='" + title + "\' content='" + content + "\'}";
    }
}
