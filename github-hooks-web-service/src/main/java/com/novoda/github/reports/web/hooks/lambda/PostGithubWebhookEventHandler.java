package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.service.issue.GithubIssue;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class PostGithubWebhookEvent implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("starting...");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();

        Reader reader = new InputStreamReader(input);
        GithubWebhookEvent payload = gson.fromJson(reader, GithubWebhookEvent.class);

        GithubIssue pullRequest = payload.pullRequest();
        String json;
        if (pullRequest != null) {
            logger.log(pullRequest.toString());
            pullRequest.setIsPullRequest(true); // @RUI the alternative to using this is a custom type adapter
            json = gson.toJson(pullRequest, GithubIssue.class);
        } else {
            logger.log("no pull request!");
            json = gson.toJson("{\"action\": \"" + payload.action() + "\"}");
        }
        logger.log(payload.toString());

        logger.log("finishing up...");
        try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
