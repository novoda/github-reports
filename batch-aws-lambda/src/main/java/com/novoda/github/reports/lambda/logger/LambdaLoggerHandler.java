package com.novoda.github.reports.lambda.logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.novoda.github.reports.batch.logger.LoggerHandler;

public class LambdaLoggerHandler implements LoggerHandler {

    private final LambdaLogger logger;

    public LambdaLoggerHandler(Context context) {
        this.logger = context.getLogger();
    }

    @Override
    public void log(String message) {
        logger.log(message);
    }

}
