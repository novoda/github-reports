package com.novoda.github.reports.batch.aws;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.batch.aws.command.BatchOptions;
import com.novoda.github.reports.data.db.LogHelper;

public class Main {

    static {
        LogHelper.turnOffJooqAd();
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }

    private void execute(String[] args) {
        BatchOptions options = new BatchOptions();
        JCommander commander = new JCommander(options);
        commander.parse(args);
    }

}
