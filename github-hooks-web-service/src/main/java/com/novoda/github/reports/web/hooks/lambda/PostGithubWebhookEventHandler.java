package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.novoda.github.reports.web.hooks.secret.InvalidSecretException;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.jetbrains.annotations.Nullable;
import org.jooq.tools.JooqLogger;

public class PostGithubWebhookEventHandler implements RequestStreamHandler {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .create();

    private PayloadVerifier payloadVerifier;
    private EventForwarder eventForwarder;
    private OutputWriter outputWriter;
    private Logger logger;

    public PostGithubWebhookEventHandler() {
        eventForwarder = EventForwarder.newInstance();
        payloadVerifier = PayloadVerifier.newInstance();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        outputWriter = OutputWriter.newInstance(output, gson);
        logger = Logger.newInstance(context);
        disableJooqLogAd();

        logger.log("λ STARTING...");

        WebhookRequest request = getRequestFrom(input);

        breakIfPayloadNotValid(request);

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

    private void breakIfPayloadNotValid(WebhookRequest request) {
        try {
            payloadVerifier.checkIfPayloadIsValid(request);
        } catch (InvalidSecretException e) {
            e.printStackTrace();
            outputWriter.outputException(e);
        }
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

}
