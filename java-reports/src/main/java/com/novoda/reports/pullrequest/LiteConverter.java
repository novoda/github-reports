package com.novoda.reports.pullrequest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

class LiteConverter {

    public LitePullRequest convert(org.eclipse.egit.github.core.PullRequest pullRequest) {
        String repoName = pullRequest.getBase().getRepo().getName();
        String repoOwnerLogin = pullRequest.getBase().getRepo().getOwner().getLogin();
        int number = pullRequest.getNumber();
        String title = pullRequest.getTitle();
        String userLogin = pullRequest.getUser().getLogin();
        LocalDate createdAt = convertToLocalDate(pullRequest.getCreatedAt());
        return new LitePullRequest(repoName, repoOwnerLogin,
                number, title,
                userLogin,
                createdAt);
    }

    private LocalDate convertToLocalDate(Date java7Date) {
        return java7Date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
