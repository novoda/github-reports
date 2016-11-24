package com.novoda.floatschedule.convert;

import java.util.Map;
import java.util.function.Predicate;

class FloatNameFilter implements Predicate<Map.Entry<String, String>> {

    private final String floatName;

    static FloatNameFilter byFloatName(String floatName) {
        return new FloatNameFilter(floatName);
    }

    private FloatNameFilter(String floatName) {
        this.floatName = floatName;
    }

    @Override
    public boolean test(Map.Entry<String, String> floatNameToGithubUserName) {
        return floatNameToGithubUserName.getKey().equalsIgnoreCase(floatName);
    }

}
