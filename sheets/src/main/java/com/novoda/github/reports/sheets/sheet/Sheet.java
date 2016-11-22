package com.novoda.github.reports.sheets.sheet;

public class Sheet {

    private Feed feed;

    public Sheet(Feed feed) {
        this.feed = feed;
    }

    public Feed getFeed() {
        return feed;
    }

    @Override
    public String toString() {
        return "Sheet{feed=" + feed + "}";
    }
}
