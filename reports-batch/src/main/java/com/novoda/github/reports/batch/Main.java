package com.novoda.github.reports.batch;

import com.beust.jcommander.JCommander;
import com.novoda.github.reports.batch.command.AwsBatchOptions;
import com.novoda.github.reports.batch.command.LocalBatchOptions;
import com.novoda.github.reports.batch.handler.AwsBombCommandHandler;
import com.novoda.github.reports.batch.handler.AwsNewCommandHandler;
import com.novoda.github.reports.batch.handler.AwsResumeCommandHandler;
import com.novoda.github.reports.batch.handler.LocalCommandHandler;
import com.novoda.github.reports.data.db.LogHelper;

public class Main {

    private static final String COMMAND_LOCAL = "local";
    private static final String COMMAND_AWS_NEW = "aws-new";
    private static final String COMMAND_AWS_RESUME = "aws-resume";
    private static final String COMMAND_AWS_BOMB = "aws-bomb";

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
        commander.addCommand(COMMAND_AWS_NEW, awsBatchOptions);
        commander.addCommand(COMMAND_AWS_RESUME, awsBatchOptions);
        commander.addCommand(COMMAND_AWS_BOMB, awsBatchOptions);

        commander.parse(args);
        String command = commander.getParsedCommand();

        if (command.equals(COMMAND_LOCAL)) {
            LocalCommandHandler commandHandler = new LocalCommandHandler();
            commandHandler.handle(localBatchOptions);
        } else if (command.equals(COMMAND_AWS_NEW)) {
            AwsNewCommandHandler commandHandler = AwsNewCommandHandler.newInstance();
            commandHandler.handle(awsBatchOptions);
        } else if (command.equals(COMMAND_AWS_RESUME)) {
            AwsResumeCommandHandler commandHandler = AwsResumeCommandHandler.newInstance();
            commandHandler.handle(awsBatchOptions);
        } else if (command.equals(COMMAND_AWS_BOMB)) {
            AwsBombCommandHandler commandHandler = AwsBombCommandHandler.newInstance();
            commandHandler.handle(awsBatchOptions);
        } else {
            throw new UnhandledCommandException(command);
        }
    }

}
