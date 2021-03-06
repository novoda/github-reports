package com.novoda.github.reports.web.hooks.lambda;

import com.google.gson.Gson;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

class OutputWriter implements Closeable {

    private OutputStream outputStream;
    private final Gson gson;

    public static OutputWriter newInstance(Gson gson) {
        return new OutputWriter(new NoOpOutputStream(), gson);
    }

    OutputWriter(OutputStream outputStream, Gson gson) {
        this.outputStream = outputStream;
        this.gson = gson;
    }

    void outputException(Exception exception) {
        safeClose();
        throw new RuntimeException(exception); // this is aws lambda-specific as it's the only way we can mark the output as erroneous
    }

    private void safeClose() {
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void writeToOutput(String message) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    public static class NoOpOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            throw new RuntimeException("No OutputStream was set!");
        }

    }
}
