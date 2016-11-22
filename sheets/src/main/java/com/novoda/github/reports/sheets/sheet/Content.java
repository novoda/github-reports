package com.novoda.github.reports.sheets.sheet;

import com.google.gson.annotations.SerializedName;

public class Content {

    private String type;

    @SerializedName("$t")
    private String value;

    public Content(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Content{type='" + type + "\', value='" + value + '\'' + "}";
    }
}
