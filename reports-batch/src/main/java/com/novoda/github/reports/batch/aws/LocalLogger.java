package com.novoda.github.reports.batch.aws;

import com.novoda.github.reports.batch.worker.Logger;

import java.util.logging.Level;

public class LocalLogger implements Logger {

    private final java.util.logging.Logger logger;

    public static LocalLogger newInstance(Class forClass) {
        return new LocalLogger(forClass);
    }

    private LocalLogger(Class forClass) {
        this.logger = java.util.logging.Logger.getLogger(forClass.getName());
    }

    @Override
    public void log(String what) {
        logger.log(Level.INFO, what);
    }

}
