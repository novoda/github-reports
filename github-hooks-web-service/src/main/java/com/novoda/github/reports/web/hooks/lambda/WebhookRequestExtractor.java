package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.io.InputStream;
import java.io.Reader;

import org.jetbrains.annotations.Nullable;

class WebhookRequestExtractor {

    private final InputStreamReaderFactory inputStreamReaderFactory;
    private final OutputWriter outputWriter;
    private final Gson gson;

    WebhookRequestExtractor(InputStreamReaderFactory inputStreamReaderFactory, OutputWriter outputWriter, Gson gson) {
        this.inputStreamReaderFactory = inputStreamReaderFactory;
        this.outputWriter = outputWriter;
        this.gson = gson;
    }

    @Nullable
    WebhookRequest extractFrom(InputStream input) throws RuntimeException {
        Reader reader = inputStreamReaderFactory.createFor(input);
        WebhookRequest request = null;

        try {
            request = gson.fromJson(reader, WebhookRequest.class);
        } catch (Exception e) {
            outputWriter.outputException(e);
        }

        return request;
    }

}
