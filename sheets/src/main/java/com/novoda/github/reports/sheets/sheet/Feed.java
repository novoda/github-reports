package com.novoda.github.reports.sheets.sheet;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Feed {

    @SerializedName("entry")
    private List<Entry> entries;

    public Feed(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return "Feed{entries=" + entries + "}";
    }
}
