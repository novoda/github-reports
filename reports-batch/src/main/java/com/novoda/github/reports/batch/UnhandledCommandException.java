package com.novoda.github.reports.batch;

class UnhandledCommandException extends Throwable {

    UnhandledCommandException(String command) {
        super(String.format("The command %s is not supported", command));
    }

}
