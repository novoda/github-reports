package com.novoda.github.reports.web.hooks.classification;

public enum EventType {

    // FIXME @RUI COMMIT_COMMENT IS THE NEW REVIEW_COMMENT
    COMMIT_COMMENT(new CommitCommentRule()),    // payload has: 'comment' object and no 'issue' or 'pull_request' objects
    ISSUE(new IssueRule()),                     // payload has: 'issue' object
    PULL_REQUEST(new PullRequestRule()),        // payload has: 'pull_request' object
    ISSUE_COMMENT(new IssueCommentRule()),      // payload has: 'issue', 'repository' and 'comment' objects
    DEPRECATED_REVIEW_COMMENT(new Deprecated_ReviewCommentRule());    // payload has: 'pull_request', 'repository' and 'comment' objects
    // FIXME @RUI this deprecated type will become the CommitComment

    private final ClassificationRule rule;

    EventType(ClassificationRule rule) {
        this.rule = rule;
    }

    public ClassificationRule getRule() {
        return rule;
    }
}
