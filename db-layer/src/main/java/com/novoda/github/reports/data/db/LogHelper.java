package com.novoda.github.reports.data.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.Constants;

public class LogHelper {

    public static void turnOffJooqAd() {
        Logger.getLogger(Constants.class.getName()).setLevel(Level.OFF);
    }

}
