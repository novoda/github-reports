package com.novoda.github.reports.service.network;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

class NextPageExtractor {

    private static final String LINK_HEADER_KEY = "Link";
    private static final String PAGE_REGEX = "[\\?|&]+page=(\\d+)[&\\w+=\\d+]*>; rel=\"next\"";

    Optional<Integer> getNextPageFrom(Response response) {
        String linkHeader = response.headers().get(LINK_HEADER_KEY);
        if (linkHeader == null) {
            return Optional.empty();
        }
        return getNextOrEmpty(linkHeader);
    }

    private Optional<Integer> getNextOrEmpty(String value) {
        Pattern pattern = Pattern.compile(PAGE_REGEX);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String group = matcher.group(1);
            return Optional.of(Integer.parseInt(group));
        }
        return Optional.empty();
    }

}
