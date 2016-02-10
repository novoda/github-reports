package com.novoda.reports.pullrequest.comment;

import com.novoda.reports.pullrequest.LitePullRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CommentInMemoryDataSource {

    private static final Map<LitePullRequest, List<Comment>> CACHE = new HashMap<>();

    public List<Comment> readComments(LitePullRequest pullRequest) {
        if (CACHE.containsKey(pullRequest)) {
            return CACHE.get(pullRequest);
        } else {
            return Collections.emptyList();
        }
    }

    public void createComments(LitePullRequest pullRequest, List<Comment> comments) {
        CACHE.put(pullRequest, comments);
    }
}
