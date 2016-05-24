package com.novoda.github.reports.github.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Response;

class NextPageExtractor {

    public Integer getNext(String ) {
        return null;
    }

    private Integer checkForRels(Response response) {
        // check if there's 'rels'
        String linkHeader = response.headers().get("Link");
        //String linkHeader = response.header("Link");
        if (linkHeader == null) {
            return null;
        }

        Pattern pattern = Pattern.compile("\\?page=(\\d)>; rel=\"next\"");
        Matcher matcher = pattern.matcher(linkHeader);
        while (matcher.find()) {
            String group = matcher.group(1);
            System.out.println(">>> " + group);
            return Integer.parseInt(group);
        }

        return null; // FIXME: 23/05/2016 too many return pts
    }

}
