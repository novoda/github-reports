package com.novoda.reports;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.pullrequest.LitePullRequest;
import com.novoda.reports.pullrequest.PullRequestFinder;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class ReportTracker {

    private final List<OrganisationRepo> repos;
    private final PullRequestService pullRequestService;
    private final PullRequestFinder pullRequestFinder;

    public ReportTracker(List<OrganisationRepo> organisationRepos,
                         PullRequestService pullRequestService,
                         PullRequestFinder pullRequestFinder) {
        this.repos = organisationRepos;
        this.pullRequestService = pullRequestService;
        this.pullRequestFinder = pullRequestFinder;
    }

    public Report track(String user, LocalDate startDate, LocalDate endDate) {
        Report.Builder reportBuilder = new Report.Builder(user);

        long mergedPrsCount = pullRequestFinder.getAllPullRequestsIn(repos)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .map(getFullDataPullRequest())
                .filter(includeMergedBy(user))
                .count();
        reportBuilder.withMergedPullRequests(mergedPrsCount);

        long createdPrsCount = pullRequestFinder.getAllPullRequestsIn(repos)
                .filter(includePullRequestsBy(user))
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .count();
        reportBuilder.withCreatedPullRequests(createdPrsCount);

        long otherPeopleCommentsCount = pullRequestFinder.getAllPullRequestsIn(repos)
                .filter(includePullRequestsBy(user))
                .flatMap(getAllComments())
                .filter(excludeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withOtherPeopleCommentsCount(otherPeopleCommentsCount);

        long usersCommentCount = pullRequestFinder.getAllPullRequestsIn(repos)
                .filter(excludePullRequestsBy(user))
                .flatMap(getAllComments())
                .filter(includeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withUsersCommentCount(usersCommentCount);

        long usersTotalCommentCount = pullRequestFinder.getAllPullRequestsIn(repos)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .flatMap(getAllComments())
                .filter(includeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withUsersTotalCommentCount(usersTotalCommentCount);

        return reportBuilder.build();
    }

    private Function<LitePullRequest, PullRequest> getFullDataPullRequest() {
        return pullRequest -> {
            try {
                return pullRequestService.getPullRequest(pullRequest::generateId, pullRequest.getNumber());
            } catch (IOException e) {
                String repoName = pullRequest.getRepoName();
                String title = pullRequest.getTitle();
                throw new IllegalStateException("FooBar for repo " + repoName + " pr " + title, e);
            }
        };
    }

    private Predicate<PullRequest> includeMergedBy(String user) {
        return pullRequest -> pullRequest.isMerged() && pullRequest.getMergedBy().getLogin().equalsIgnoreCase(user);
    }

    private Predicate<LitePullRequest> includePullRequestsBy(String user) {
        return pullRequest -> pullRequest.getUserLogin().equalsIgnoreCase(user);
    }

    private Function<LitePullRequest, Stream<CommitComment>> getAllComments() {
        return pullRequest -> {
            try {
                return pullRequestService.getComments(pullRequest::generateId, pullRequest.getNumber()).stream();
            } catch (IOException e) {
                String repoName = pullRequest.getRepoName();
                String title = pullRequest.getTitle();
                throw new IllegalStateException("FooBar for repo " + repoName + ", pr " + title, e);
            }
        };
    }

    private Predicate<CommitComment> excludeCommentsBy(String user) {
        return comment -> !comment.getUser().getLogin().equalsIgnoreCase(user);
    }

    private Predicate<CommitComment> commentedBetween(LocalDate startDate, LocalDate endDate) {
        return commitComment -> {
            LocalDate createdAt = convertToLocalDate(commitComment.getCreatedAt());
            return createdAt.isAfter(startDate.minusDays(1))
                    && createdAt.isBefore(endDate.plusDays(1));
        };
    }

    private LocalDate convertToLocalDate(Date java7Date) {
        return java7Date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
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

    private Predicate<CommitComment> includeCommentsBy(String user) {
        return comment -> comment.getUser().getLogin().equalsIgnoreCase(user);
    }

}
