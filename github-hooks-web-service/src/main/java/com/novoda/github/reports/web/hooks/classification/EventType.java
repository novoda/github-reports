package com.novoda.github.reports.web.hooks.classification;

public enum EventType {

    REVIEW_COMMENT(new ReviewCommentRule()),    // payload has: 'comment', 'pull_request' and 'repository' objects
    ISSUE(new IssueRule()),                     // payload has: 'issue' and 'repository' objects
    PULL_REQUEST(new PullRequestRule()),        // payload has: 'pull_request', 'repository' objects
    ISSUE_COMMENT(new IssueCommentRule()),      // payload has: 'issue', 'repository' and 'comment' objects
    COMMIT_COMMENT(new CommitCommentRule());    // payload has: 'repository' and 'comment' objects

    private final ClassificationRule rule;

    EventType(ClassificationRule rule) {
        this.rule = rule;
    }

    public ClassificationRule getRule() {
        return rule;
    }
}
