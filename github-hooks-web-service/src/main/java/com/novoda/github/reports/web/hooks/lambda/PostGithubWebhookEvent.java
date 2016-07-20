package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class PostGithubWebhookEvent implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String input, Context context) {
        return "JUST GOT: " + input;
    }
}
