package com.novoda.github.reports.batch;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.batch.command.AwsBatchOptions;
import com.novoda.github.reports.batch.command.LocalBatchOptions;
import com.novoda.github.reports.batch.handler.AwsCommandHandler;
import com.novoda.github.reports.batch.handler.LocalCommandHandler;
import com.novoda.github.reports.data.db.LogHelper;

public class Main {

    private static final String COMMAND_LOCAL = "local";
    private static final String COMMAND_AWS = "aws";

    static {
        LogHelper.turnOffJooqAd();
    }

    public static void main(String[] args) throws Throwable {
        new Main().execute(args);
    }

    private void execute(String[] args) throws Throwable {
        LocalBatchOptions localBatchOptions = new LocalBatchOptions();
        AwsBatchOptions awsBatchOptions = new AwsBatchOptions();

        JCommander commander = new JCommander();
        commander.addCommand(COMMAND_LOCAL, localBatchOptions);
        commander.addCommand(COMMAND_AWS, awsBatchOptions);

        commander.parse(args);
        String command = commander.getParsedCommand();

        if (command.equals(COMMAND_LOCAL)) {
            LocalCommandHandler commandHandler = new LocalCommandHandler();
            commandHandler.handle(localBatchOptions);
        } else if (command.equals(COMMAND_AWS)) {
            AwsCommandHandler commandHandler = AwsCommandHandler.newInstance();
            commandHandler.handle(awsBatchOptions);
        } else {
            throw new UnhandledCommandException(command);
        }
    }

}
