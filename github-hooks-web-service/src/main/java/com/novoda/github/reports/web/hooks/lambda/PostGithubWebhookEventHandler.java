package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.handler.UnhandledEventException;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.stream.Collectors;

import org.jooq.tools.JooqLogger;

public class PostGithubWebhookEventHandler implements RequestStreamHandler {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .create();

    private EventForwarder eventForwarder;

    public PostGithubWebhookEventHandler() {
        eventForwarder = EventForwarder.newInstance();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        LambdaLogger logger = getLogger(context);
        disableJooqLogAd();

        logger.log("> λ STARTING...");

        GithubWebhookEvent event = getEventFrom(input);
        try {
            logger.log("> FORWARDING EVENT...");
            eventForwarder.forward(event);
            writeToOutput(output, convertEventToJson(event));
            logger.log("> HANDLED EVENT: " + event.toString());
        } catch (UnhandledEventException e) {
            logger.log("> ! ERROR: Failed to forward an event (" + event.toString() + "). " + e.getMessage());
            outputException(output, e);
            e.printStackTrace();
        }
    }

    private String convertEventToJson(GithubWebhookEvent event) {
        try {
            return gson.toJson(event, GithubWebhookEvent.class);
        } catch (Exception e) {
            return wrapInError("Failed to convert event to json.");
        }
    }

    private void disableJooqLogAd() {
        JooqLogger.globalThreshold(JooqLogger.Level.WARN);
    }

    private GithubWebhookEvent getEventFrom(InputStream input) {
        Reader reader = new InputStreamReader(input);
        return gson.fromJson(reader, GithubWebhookEvent.class);
    }

    private String getPostBody(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().parallel().collect(Collectors.joining("\n"));
    }

    private LambdaLogger getLogger(Context context) {
        return context == null ? System.out::println : context.getLogger();
    }

    private void outputException(OutputStream output, Exception exception) {
        writeToOutput(output, wrapInError(exception.getMessage()));
    }

    private String wrapInError(String message) {
        return "{\"error\": \"" + message + "\"}";
    }

    private void writeToOutput(OutputStream output, String message) {
        try {
            writeToOutputFor(output, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToOutputFor(OutputStream output, String message) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
