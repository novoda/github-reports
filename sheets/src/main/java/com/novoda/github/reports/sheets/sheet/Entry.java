package com.novoda.github.reports.sheets.sheet;

import com.google.gson.annotations.SerializedName;

public class Entry {

    @SerializedName("title")
    private Content key;

    @SerializedName("content")
    private Content value;

    public Entry(Content key, Content value) {
        this.key = key;
        this.value = value;
    }

    public Content getKey() {
        return key;
    }

    public Content getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Entry{key=" + key + ", value=" + value + "}";
    }
}
