package com.novoda.github.reports.web.hooks.classification;

public enum EventType {

    COMMIT_COMMENT(new CommitCommentRule()),    // payload has 'comment' object and no 'issue' or 'pull_request' objects
    ISSUE(new IssueRule()),                     // payload has 'issue' object
    PULL_REQUEST(new PullRequestRule()),        // payload has 'pull_request' object
    ISSUE_COMMENT(new IssueCommentRule()),      // payload has both 'issue' and 'comment' objects
    REVIEW_COMMENT(new ReviewCommentRule());    // payload has both 'pull_request' and 'comment' objects

    private final ClassificationRule rule;

    EventType(ClassificationRule rule) {
        this.rule = rule;
    }

    public ClassificationRule getRule() {
        return rule;
    }
}
