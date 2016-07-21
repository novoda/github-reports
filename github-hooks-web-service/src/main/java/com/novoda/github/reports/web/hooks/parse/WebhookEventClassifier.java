package com.novoda.github.reports.web.hooks.parse;

import com.novoda.github.reports.web.hooks.EventType;
import com.novoda.github.reports.web.hooks.lambda.GithubWebhookEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WebhookEventClassifier {

    private static final Map<EventType, ClassificationRule> RULES = new HashMap<>(5);
    static {
        RULES.put(EventType.COMMIT_COMMENT, new CommitCommentRule());
        RULES.put(EventType.ISSUE, new IssueRule());
        RULES.put(EventType.PULL_REQUEST, new PullRequestRule());
        RULES.put(EventType.ISSUE_COMMENT, new IssueCommentRule());
        RULES.put(EventType.REVIEW_COMMENT, new ReviewCommentRule());
    }

    public EventType classify(GithubWebhookEvent event) {
        return RULES.entrySet()
                .stream()
                .filter(entry -> entry.getValue().checkFor(event))
                .findFirst()
                .orElseThrow(supplyClassificationException())
                .getKey();
    }

    private Supplier<IllegalStateException> supplyClassificationException() {
        return () -> new IllegalStateException("Unable to classify.");
    }

}
