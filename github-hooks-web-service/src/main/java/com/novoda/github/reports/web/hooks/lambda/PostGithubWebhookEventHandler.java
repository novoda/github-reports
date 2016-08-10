package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.jooq.tools.JooqLogger;

public class PostGithubWebhookEventHandler implements RequestStreamHandler {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .create();

    private EventForwarder eventForwarder;
    private Logger logger;
    private OutputWriter outputWriter;

    public PostGithubWebhookEventHandler() {
        eventForwarder = EventForwarder.newInstance();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        logger = Logger.newInstance(context);
        outputWriter = OutputWriter.newInstance(output, gson);
        disableJooqLogAd();

        logger.log("λ STARTING...");

        // ---
//        String body = getPostBody(new BufferedInputStream(input));
//        logger.log(body);
        // ---

        // TODO extract secret (using a collaborator)
        // TODO calculate hex (using collab)
        // TODO - properties file reader to read the key
        // TODO check if keys match

        WebhookRequest request = getRequestFrom(input);
        GithubWebhookEvent event = getEventFrom(request);

        try {
            logger.log("FORWARDING EVENT...");
            eventForwarder.forward(event);
            outputWriter.outputEvent(event);
            logger.log("HANDLED EVENT: " + event.toString());
        } catch (Exception e) {
            logger.log("ERROR: Failed to forward an event (" + event.toString() + "). " + e.getMessage());
            outputWriter.outputException(e);
        } finally {
            closeOutputWriter();
        }
    }

    private void disableJooqLogAd() {
        JooqLogger.globalThreshold(JooqLogger.Level.WARN);
    }

    @Nullable
    private WebhookRequest getRequestFrom(InputStream input) {
        Reader reader = new InputStreamReader(input);
        try {
            return gson.fromJson(reader, WebhookRequest.class);
        } catch (Exception e) {
            outputWriter.outputException(e);
        }
        return null;
    }

    @Nullable
    private GithubWebhookEvent getEventFrom(WebhookRequest request) {
        if (request.event() == null) {
            outputWriter.outputException(new NullPointerException("event is null"));
        }
        return request.event();
    }

    private void closeOutputWriter() {
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPostBody(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().parallel().collect(Collectors.joining("\n"));
    }
}
