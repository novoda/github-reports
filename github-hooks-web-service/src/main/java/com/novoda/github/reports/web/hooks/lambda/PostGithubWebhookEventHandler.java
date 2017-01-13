package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.EventForwarder;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jooq.tools.JooqLogger;

public class PostGithubWebhookEventHandler implements RequestStreamHandler {

    private final EventForwarder eventForwarder;
    private final OutputWriter outputWriter;
    private final Logger logger;

    private final PayloadVerificationRunner payloadVerificationRunner;
    private final WebhookRequestExtractor webhookRequestExtractor;
    private final EventExtractor eventExtractor;

    public PostGithubWebhookEventHandler() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
                .create();

        PayloadVerifier payloadVerifier = PayloadVerifier.newInstance();
        InputStreamReaderFactory inputStreamReaderFactory = new InputStreamReaderFactory();
        OutputWriter outputWriter = OutputWriter.newInstance(gson);

        this.payloadVerificationRunner = new PayloadVerificationRunner(payloadVerifier, outputWriter);
        this.webhookRequestExtractor = new WebhookRequestExtractor(inputStreamReaderFactory, outputWriter, gson);
        this.eventExtractor = new EventExtractor(outputWriter, gson);

        this.eventForwarder = EventForwarder.newInstance();
        this.outputWriter = OutputWriter.newInstance(gson);
        this.logger = Logger.newInstance();
    }

    PostGithubWebhookEventHandler(
            PayloadVerificationRunner payloadVerificationRunner,
            WebhookRequestExtractor webhookRequestExtractor,
            EventExtractor eventExtractor,
            EventForwarder eventForwarder,
            OutputWriter outputWriter,
            Logger logger) {

        this.payloadVerificationRunner = payloadVerificationRunner;
        this.webhookRequestExtractor = webhookRequestExtractor;
        this.eventExtractor = eventExtractor;
        this.eventForwarder = eventForwarder;
        this.outputWriter = outputWriter;
        this.logger = logger;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        disableJooqLogAd();
        logger.setLoggerFrom(context);
        outputWriter.setOutputStream(output);

        logger.log("λ STARTING...");

        try {
            WebhookRequest request = webhookRequestExtractor.extractFrom(input);
            handle(request);
        } finally {
            closeOutputWriter();
        }
    }

    private void handle(WebhookRequest request) {

        payloadVerificationRunner.verify(request);

        GithubWebhookEvent event = eventExtractor.extractFrom(request);

        try {
            logger.log("FORWARDING EVENT...");
            eventForwarder.forward(event);
            outputWriter.outputEvent(event);
            logger.log("HANDLED EVENT: " + event.toString());
        } catch (Exception e) {
            logger.log("ERROR: Failed to forward an event (" + event.toString() + "). " + e.getMessage());
            outputWriter.outputException(e);
        }
    }

    private void disableJooqLogAd() {
        JooqLogger.globalThreshold(JooqLogger.Level.WARN);
    }

    private void closeOutputWriter() {
        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
