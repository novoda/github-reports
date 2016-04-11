package com.novoda.reports;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class NewLineLogger implements LambdaLogger {

    private final LambdaLogger lambdaLogger;

    public NewLineLogger(LambdaLogger lambdaLogger) {
        this.lambdaLogger = lambdaLogger;
    }

    @Override
    public void log(String string) {
        lambdaLogger.log(string + "\n");
    }
}
