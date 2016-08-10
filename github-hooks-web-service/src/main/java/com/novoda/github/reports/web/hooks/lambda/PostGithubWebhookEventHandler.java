package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.novoda.github.reports.web.hooks.secret.SecretException;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.BufferedInputStream;
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
    private OutputWriter outputWriter;
    private Logger logger;

    private PayloadVerifier payloadVerifier;

    public PostGithubWebhookEventHandler() {
        eventForwarder = EventForwarder.newInstance();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        payloadVerifier = PayloadVerifier.newInstance();

        outputWriter = OutputWriter.newInstance(output, gson);
        logger = Logger.newInstance(context);
        disableJooqLogAd();

        logger.log("λ STARTING...");

        //logRequestBody(input);

        WebhookRequest request = getRequestFrom(input);

        try {
            System.out.println(payloadVerifier.checkIfPayloadIsValid(request));
        } catch (SecretException e) {
            e.printStackTrace();
            outputWriter.outputException(e);
        }

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
        if (request.body() == null) {
            outputWriter.outputException(new NullPointerException("event is null"));
        }
        return gson.fromJson(request.body(), GithubWebhookEvent.class);
    }

    private void closeOutputWriter() {
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO remove
    private void logRequestBody(InputStream input) {
        String body = getPostBody(new BufferedInputStream(input));
        logger.log(body);
    }

    // TODO remove
    private String getPostBody(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().parallel().collect(Collectors.joining("\n"));
    }
}
