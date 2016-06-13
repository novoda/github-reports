package com.novoda.github.reports.aws.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AmazonConfigurationConverter {

    private final Gson gson;

    public static AmazonConfigurationConverter newInstance() {
        Gson gson = new GsonBuilder().create();
        return new AmazonConfigurationConverter(gson);
    }

    private AmazonConfigurationConverter(Gson gson) {
        this.gson = gson;
    }

    public AmazonConfiguration fromJson(String json) {
        return null;
    }

    public String toJson(AmazonConfiguration configuration) {
        return null;
    }

}
