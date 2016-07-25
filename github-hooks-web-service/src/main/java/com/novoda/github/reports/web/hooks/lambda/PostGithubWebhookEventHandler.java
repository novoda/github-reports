package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.web.hooks.handler.HandlerRouter;
import com.novoda.github.reports.web.hooks.handler.UnhandledEventException;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class PostGithubWebhookEventHandler implements RequestStreamHandler {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .create();

    private HandlerRouter handlerRouter;

    public PostGithubWebhookEventHandler() {
        handlerRouter = HandlerRouter.newInstance();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        LambdaLogger logger = getLogger(context);

        GithubWebhookEvent event = getEventFrom(input);
        try {
            handlerRouter.route(event);
        } catch (UnhandledEventException e) {
            e.printStackTrace();
        }

        logger.log(event.toString());
        debug_writeToOutputFor(output, event.toString());
    }

    private GithubWebhookEvent getEventFrom(InputStream input) {
        Reader reader = new InputStreamReader(input);
        return gson.fromJson(reader, GithubWebhookEvent.class);
    }

    private LambdaLogger getLogger(Context context) {
        return context == null ? System.out::println : context.getLogger();
    }

    private void debug_writeToOutputFor(OutputStream output, String json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
