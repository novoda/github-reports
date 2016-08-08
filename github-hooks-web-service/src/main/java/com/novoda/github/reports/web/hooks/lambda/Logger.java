package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Logger {

    private final LambdaLogger logger;

    public static Logger newInstance(Context context) {
        LambdaLogger logger = context == null ? System.out::println : context.getLogger();
        return new Logger(logger);
    }

    private Logger(LambdaLogger logger) {
        this.logger = logger;
    }

    void log(String message) {
        logger.log("> " + message);
    }
}
