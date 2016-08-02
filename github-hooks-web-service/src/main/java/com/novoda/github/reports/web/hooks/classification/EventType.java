package com.novoda.github.reports.web.hooks.classification;

public enum EventType {

    // FIXME @RUI COMMIT_COMMENT IS THE NEW REVIEW_COMMENT
    REVIEW_COMMENT(new ReviewCommentRule()),    // payload has: 'comment', 'pull_request' and 'repository' objects
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
