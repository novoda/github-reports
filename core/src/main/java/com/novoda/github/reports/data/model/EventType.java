package com.novoda.github.reports.data.model;

public enum EventType {

    ISSUE_OPEN(100),
    ISSUE_CLOSE(101),
    ISSUE_COMMENT_ADD(102),
    ISSUE_COMMENT_EDIT(103),
    ISSUE_COMMENT_REMOVE(104),
    ISSUE_LABEL_ADD(105),
    ISSUE_LABEL_REMOVE(106),
    PULL_REQUEST_OPEN(200),
    PULL_REQUEST_CLOSE(201),
    PULL_REQUEST_COMMENT_ADD(202),
    PULL_REQUEST_COMMENT_EDIT(203),
    PULL_REQUEST_COMMENT_REMOVE(204),
    PULL_REQUEST_LABEL_ADD(205),
    PULL_REQUEST_LABEL_REMOVE(206),
    COMMIT(300),
    BRANCH_DELETE(301);

    EventType(int n) {
        value = n;
    }

    public final int value;

    public int getValue() {
        return value;
    }

}
