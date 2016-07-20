package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.service.pullrequest.GithubPullRequest;
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

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();
        Reader reader = new InputStreamReader(input);
        GithubWebhookEvent payload = gson.fromJson(reader, GithubWebhookEvent.class);

        String json = "{}";
        GithubPullRequest pullRequest = payload.pullRequest();
        if (pullRequest != null) {
            logger.log(pullRequest.toString());
        } else {
            logger.log("no pull request!");
            json = gson.toJson("{\"action\": \"" + payload.action() + "\"}");
        }
        logger.log(payload.toString());

        try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
