package com.novoda.github.reports.lambda.worker;

import com.amazonaws.services.lambda.runtime.Context;
import com.novoda.github.reports.batch.worker.Logger;

public class LambdaLogger implements Logger {

    private final com.amazonaws.services.lambda.runtime.LambdaLogger logger;

    public LambdaLogger(Context context) {
        this.logger = context.getLogger();
    }

    @Override
    public void log(String what) {
        logger.log(what);
    }

}
