package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class PostGithubWebhookEvent implements RequestHandler<Map<String, Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("received : " + input);
        return jsonHandler(input, context);
    }

    public String jsonHandler(Map<String, Object> data, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("\n>>> function name: " + context.getFunctionName());
        logger.log("\n>>> data: " + data);
        return mapToString(data);
    }

    private String mapToString(Map<String, Object> data) {
        final String[] result = {""};
        data.forEach((s, o) -> result[0] += valueOrNull(s) + ", " + valueOrNull(o) + "\n");
        return result[0];
    }

    private String valueOrNull(Object o) {
        return o == null ? "[null]" : o.toString();
    }
}
