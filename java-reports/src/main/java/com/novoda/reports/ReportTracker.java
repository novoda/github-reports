package com.novoda.reports;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.pullrequest.FullPullRequest;
import com.novoda.reports.pullrequest.LitePullRequest;
import com.novoda.reports.pullrequest.PullRequestFinder;
import com.novoda.reports.pullrequest.comment.Comment;
import com.novoda.reports.pullrequest.comment.CommentFinder;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

class ReportTracker {

    private final List<OrganisationRepo> repos;
    private final PullRequestFinder pullRequestFinder;
    private final CommentFinder commentFinder;

    public ReportTracker(List<OrganisationRepo> organisationRepos,
                         PullRequestFinder pullRequestFinder,
                         CommentFinder commentFinder) {
        this.repos = organisationRepos;
        this.pullRequestFinder = pullRequestFinder;
        this.commentFinder = commentFinder;
    }

    public Report track(String user, LocalDate startDate, LocalDate endDate) {
        Report.Builder reportBuilder = new Report.Builder(user);

        long mergedPrsCount = repos
                .stream()
                .flatMap(pullRequestFinder::streamLitePullRequests)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .map(pullRequestFinder::getFullPullRequest)
                .filter(includeMergedBy(user))
                .count();
        reportBuilder.withMergedPullRequests(mergedPrsCount);

        long createdPrsCount = repos
                .stream()
                .flatMap(pullRequestFinder::streamLitePullRequests)
                .filter(includePullRequestsBy(user))
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .count();
        reportBuilder.withCreatedPullRequests(createdPrsCount);

        long otherPeopleCommentsCount = repos
                .stream()
                .flatMap(pullRequestFinder::streamLitePullRequests)
                .filter(includePullRequestsBy(user))
                .flatMap(commentFinder::streamComments)
                .filter(excludeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withOtherPeopleCommentsCount(otherPeopleCommentsCount);

        long usersCommentCount = repos
                .stream()
                .flatMap(pullRequestFinder::streamLitePullRequests)
                .filter(excludePullRequestsBy(user))
                .flatMap(commentFinder::streamComments)
                .filter(includeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withUsersCommentCount(usersCommentCount);

        long usersTotalCommentCount = repos
                .stream()
                .flatMap(pullRequestFinder::streamLitePullRequests)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .flatMap(commentFinder::streamComments)
                .filter(includeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withUsersTotalCommentCount(usersTotalCommentCount);

        return reportBuilder.build();
    }

    private Predicate<FullPullRequest> includeMergedBy(String user) {
        return pullRequest -> pullRequest.isMerged() && pullRequest.getMergedByUserLogin().equalsIgnoreCase(user);
    }

    private Predicate<LitePullRequest> includePullRequestsBy(String user) {
        return pullRequest -> pullRequest.getUserLogin().equalsIgnoreCase(user);
    }

    private Predicate<Comment> excludeCommentsBy(String user) {
        return comment -> !comment.getUserLogin().equalsIgnoreCase(user);
    }

    private Predicate<Comment> commentedBetween(LocalDate startDate, LocalDate endDate) {
        return commitComment -> {
            LocalDate createdAt = commitComment.getCreatedAt();
            return createdAt.isAfter(startDate.minusDays(1))
                    && createdAt.isBefore(endDate.plusDays(1));
        };
    }

    private Predicate<LitePullRequest> pullRequestCreatedBetween(LocalDate startDate, LocalDate endDate) {
        return pullRequest -> {
            LocalDate createdAt = pullRequest.getCreatedAt();
            return createdAt.isAfter(startDate.minusDays(1))
                    && createdAt.isBefore(endDate.plusDays(1));
        };
    }

    private Predicate<LitePullRequest> excludePullRequestsBy(String user) {
        return pullRequest -> !pullRequest.getUserLogin().equalsIgnoreCase(user);
    }

    private Predicate<Comment> includeCommentsBy(String user) {
        return comment -> comment.getUserLogin().equalsIgnoreCase(user);
    }

}
