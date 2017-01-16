package com.novoda.github.reports.web.hooks.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Logger {

    private LambdaLogger logger;

    public static Logger newInstance() {
        LambdaLogger logger = new SystemOutLogger();
        return new Logger(logger);
    }

    Logger(LambdaLogger logger) {
        this.logger = logger;
    }

    void setLoggerFrom(Context context) {
        logger = context.getLogger();
    }

    void log(String message) {
        logger.log("> " + message);
    }

    public static class SystemOutLogger implements LambdaLogger {
        @Override
        public void log(String string) {
            System.out.println(string);
        }
    }
}
