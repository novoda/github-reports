package com.novoda.reports.pullrequest.comment;

import java.time.LocalDate;

public class Comment {

    private final String userLogin;
    private final LocalDate createdAt;

    public Comment(String userLogin, LocalDate createdAt) {
        this.userLogin = userLogin;
        this.createdAt = createdAt;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
