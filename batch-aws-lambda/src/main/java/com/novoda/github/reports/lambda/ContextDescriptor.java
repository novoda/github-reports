package com.novoda.github.reports.lambda;

import com.amazonaws.services.lambda.runtime.Context;

class ContextDescriptor {

    private Context context;

    static ContextDescriptor from(Context context) {
        return new ContextDescriptor(context);
    }

    private ContextDescriptor(Context context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return String.format(
                "AWS Request ID: %s\n" +
                        "Log Group/Stream: %s/%s\n" +
                        "Function: %s@%s\n",
                context.getAwsRequestId(),
                context.getLogGroupName(),
                context.getLogStreamName(),
                context.getFunctionName(),
                context.getFunctionVersion()
        );
    }
}
