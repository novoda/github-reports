package com.novoda.github.reports.service.network;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

class PageExtractor {

    private static final String LINK_HEADER_KEY = "Link";
    private static final String PAGE_REGEX = "[\\?|&]+page=(\\d+)[&\\w+=\\d+]*>; rel=\"%s\"";

    Optional<Integer> getPage(String pageKey, Response response) {
        String linkHeader = response.headers().get(LINK_HEADER_KEY);
        if (linkHeader == null) {
            return Optional.empty();
        }
        return getPageOrEmpty(pageKey, linkHeader);
    }

    private Optional<Integer> getPageOrEmpty(String page, String searchTarget) {
        String regex = String.format(PAGE_REGEX, page);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(searchTarget);
        if (matcher.find()) {
            String group = matcher.group(1);
            return Optional.of(Integer.parseInt(group));
        }
        return Optional.empty();
    }

}
