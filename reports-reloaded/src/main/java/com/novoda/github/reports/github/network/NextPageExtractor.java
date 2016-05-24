package com.novoda.github.reports.github.network;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

public class NextPageExtractor {

    private static final String PAGE_REGEX = "\\?page=(\\d)>; rel=\"next\"";

    public Optional<Integer> getNextPageFrom(Response response) {
        String linkHeader = response.headers().get("Link");
        if (linkHeader == null) {
            return null;
        }
        return getNextOrNull(linkHeader);
    }

    private Optional<Integer> getNextOrNull(String value) {
        Pattern pattern = Pattern.compile(PAGE_REGEX);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String group = matcher.group(1);
            return Optional.of(Integer.parseInt(group));
        }
        return Optional.empty();
    }

}
