package com.novoda.github.reports.web.hooks.classification;

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

    private final Map<EventType, ClassificationRule> rules;

    public static WebhookEventClassifier newInstance() {
        return new WebhookEventClassifier(RULES);
    }

    WebhookEventClassifier(Map<EventType, ClassificationRule> rules) {
        this.rules = rules;
    }

    public EventType classify(GithubWebhookEvent event) throws ClassificationException {
        return rules.entrySet() // TODO code for the possibility of more than one rule matching
                .stream()
                .filter(entry -> entry.getValue().check(event))
                .findFirst()
                .orElseThrow(supplyClassificationExceptionFor(event))
                .getKey();
    }

    private Supplier<ClassificationException> supplyClassificationExceptionFor(GithubWebhookEvent event) {
        return () -> new ClassificationException(event);
    }

}
