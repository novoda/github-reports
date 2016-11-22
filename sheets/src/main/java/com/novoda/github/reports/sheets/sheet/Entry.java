package com.novoda.github.reports.sheets.sheet;

public class Entry {

    private String title;
    private String content;

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
