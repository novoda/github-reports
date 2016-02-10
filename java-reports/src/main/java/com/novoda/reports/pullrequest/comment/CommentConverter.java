package com.novoda.reports.pullrequest.comment;

import org.eclipse.egit.github.core.CommitComment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

class CommentConverter {

    public Comment convert(CommitComment commitComment) {
        String userLogin = commitComment.getUser().getLogin();
        LocalDate createdAt = convertToLocalDate(commitComment.getCreatedAt());
        return new Comment(userLogin, createdAt);
    }

    private LocalDate convertToLocalDate(Date java7Date) {
        return java7Date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
