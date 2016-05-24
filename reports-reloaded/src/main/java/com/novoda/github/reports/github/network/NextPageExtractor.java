package com.novoda.github.reports.github.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NextPageExtractor {

    private static final String PAGE_REGEX = "\\?page=(\\d)>; rel=\"next\"";

    public Integer getNext(String value) {

        Pattern pattern = Pattern.compile(PAGE_REGEX);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String group = matcher.group(1);
            return Integer.parseInt(group);
        }

        return null;
    }

}
