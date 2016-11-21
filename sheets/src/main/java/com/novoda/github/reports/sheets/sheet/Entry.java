package com.novoda.github.reports.sheets.sheet;

import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("title")
    private Content content;

    Entry(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Entry{content=" + content + "}";
    }
}
