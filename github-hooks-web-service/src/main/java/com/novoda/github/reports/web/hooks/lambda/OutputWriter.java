package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

class OutputWriter implements Closeable {

    private final OutputStream outputStream;
    private final Gson gson;

    public static OutputWriter newInstance(OutputStream outputStream, Gson gson) {
        return new OutputWriter(outputStream, gson);
    }

    private OutputWriter(OutputStream outputStream, Gson gson) {
        this.outputStream = outputStream;
        this.gson = gson;
    }

    void outputException(Exception exception) {
        outputInfo(wrapInError(exception.getMessage()));
    }

    void outputEvent(GithubWebhookEvent event) {
        try {
            outputInfo(gson.toJson(event, GithubWebhookEvent.class));
        } catch (Exception e) {
            outputException(e);
        }
    }

    private void outputInfo(String message) {
        try {
            writeToOutput(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String wrapInError(String message) {
        return "{\"error\": \"" + message + "\"}";
    }

    private void writeToOutput(String message) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
