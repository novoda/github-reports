package com.novoda.github.reports.web.hooks.classification;

public enum EventType {

    COMMIT_COMMENT,     // payload has 'comment' object and no 'issue' or 'pull_request' objects
    ISSUE,              // payload has 'issue' object
    PULL_REQUEST,       // payload has 'pull_request' object
    ISSUE_COMMENT,      // payload has both 'issue' and 'comment' objects
    REVIEW_COMMENT      // payload has both 'pull_request' and 'comment' objects

}
