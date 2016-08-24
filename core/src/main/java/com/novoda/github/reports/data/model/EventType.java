package com.novoda.github.reports.data.model;

public enum EventType {

    ISSUE_OPEN(100),
    ISSUE_CLOSE(101),
    ISSUE_COMMENT(102),
    ISSUE_LABEL_ADD(103),
    ISSUE_LABEL_REMOVE(104),
    ISSUE_REACTION(110),
    PULL_REQUEST_OPEN(200),
    PULL_REQUEST_CLOSE(201),
    PULL_REQUEST_COMMENT(202),
    PULL_REQUEST_LABEL_ADD(203),
    PULL_REQUEST_LABEL_REMOVE(204),
    PULL_REQUEST_MERGE(205),
    PULL_REQUEST_REACTION(210),
    BRANCH_DELETE(300);

    EventType(int n) {
        value = n;
    }

    private final int value;

    public int getValue() {
        return value;
    }

}
