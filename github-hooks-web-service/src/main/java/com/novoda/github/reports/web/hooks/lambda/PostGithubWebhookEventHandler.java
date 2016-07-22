package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.novoda.github.reports.web.hooks.EventType;
import com.novoda.github.reports.web.hooks.parse.PullRequestParser;
import com.novoda.github.reports.web.hooks.parse.WebhookEventClassifier;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class PostGithubWebhookEventHandler implements RequestStreamHandler {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .create();

    private WebhookEventClassifier eventClassifier;

    public PostGithubWebhookEventHandler() {
        eventClassifier = new WebhookEventClassifier();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        LambdaLogger logger = getLogger(context);

        GithubWebhookEvent event = getEventFrom(input);
        EventType eventType = eventClassifier.classify(event);

        if (eventType == EventType.PULL_REQUEST) {
            PullRequestParser pullRequestParser = new PullRequestParser();
            GithubIssue githubIssue = pullRequestParser.from(event).get();
            // TODO persist
        } else if (eventType == EventType.ISSUE) {
            // TODO ...
        }

        debug_logPullRequest(logger, event);

        logger.log(event.toString());
        debug_writeToOutputFor(output, event.toString());
    }

    private LambdaLogger getLogger(Context context) {
        return context == null ? System.out::println : context.getLogger();
    }

    private void debug_logPullRequest(LambdaLogger logger, GithubWebhookEvent event) {
        GithubIssue pullRequest = event.pullRequest();
        String json;
        if (pullRequest != null) {
            logger.log(pullRequest.toString());
            pullRequest.setIsPullRequest(true); // @RUI the alternative to using this is a custom type adapter
            json = gson.toJson(pullRequest, GithubIssue.class);
        } else {
            logger.log("no pull request!");
            json = gson.toJson("{\"action\": \"" + event.action() + "\"}");
        }
        logger.log(json);
    }

    private GithubWebhookEvent getEventFrom(InputStream input) {
        Reader reader = new InputStreamReader(input);
        return gson.fromJson(reader, GithubWebhookEvent.class);
    }

    private void debug_writeToOutputFor(OutputStream output, String json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
