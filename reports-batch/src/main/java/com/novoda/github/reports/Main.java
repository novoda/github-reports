package com.novoda.github.reports;

import com.novoda.github.reports.batch.DebugClient;

public class Main {

    private void execute(String[] args) {
        System.out.println("This will be reports-batch");
        DebugClient.getComments();
    }

    public static void main(String[] args) {
        new Main().execute(args);
    }
}
