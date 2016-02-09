package com.novoda.reports;

import com.novoda.reports.organisation.OrganisationRepo;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.PullRequestService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PullRequestTracker {

    private final List<OrganisationRepo> repos;
    private final PullRequestService pullRequestService;

    private List<PullRequest> allPullRequests;

    public PullRequestTracker(List<OrganisationRepo> organisationRepos, PullRequestService pullRequestService) {
        this.repos = organisationRepos;
        this.pullRequestService = pullRequestService;
    }

    public Report track(String user, LocalDate startDate, LocalDate endDate) {
        Report.Builder reportBuilder = new Report.Builder(user);

        long mergedPrsCount = getAllPullRequestsIn(repos)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .map(getFullDataPullRequest())
                .filter(includeMergedBy(user))
                .count();
        reportBuilder.withMergedPullRequests(mergedPrsCount);

        long createdPrsCount = getAllPullRequestsIn(repos)
                .filter(includePullRequestsBy(user))
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .count();
        reportBuilder.withCreatedPullRequests(createdPrsCount);

        long otherPeopleCommentsCount = getAllPullRequestsIn(repos)
                .filter(includePullRequestsBy(user))
                .flatMap(getAllComments())
                .filter(excludeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withOtherPeopleCommentsCount(otherPeopleCommentsCount);

        long usersCommentCount = getAllPullRequestsIn(repos)
                .filter(excludePullRequestsBy(user))
                .flatMap(getAllComments())
                .filter(includeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withUsersCommentCount(usersCommentCount);

        long usersTotalCommentCount = getAllPullRequestsIn(repos)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .flatMap(getAllComments())
                .filter(includeCommentsBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
        reportBuilder.withUsersTotalCommentCount(usersTotalCommentCount);

        return reportBuilder.build();
    }

    private Stream<PullRequest> getAllPullRequestsIn(List<OrganisationRepo> repositories) {
        if (allPullRequests == null) {
            allPullRequests = repositories
                    .parallelStream()
                    .flatMap(repository -> {
                        try {
                            return pullRequestService
                                    .getPullRequests(repository::getId, "all")
                                    .stream();
                        } catch (IOException e) {
                            String repoName = repository.getName();
                            throw new IllegalStateException("Foo on repo " + repoName, e);
                        }
                    })
                    .collect(Collectors.toList());
        }
        return allPullRequests
                .stream();
    }

    private Function<PullRequest, PullRequest> getFullDataPullRequest() {
        return pullRequest -> {
            Repository repo = pullRequest.getHead().getRepo();
            try {
                return pullRequestService.getPullRequest(repo, pullRequest.getNumber());
            } catch (IOException e) {
                String repoName = repo.getName();
                String title = pullRequest.getTitle();
                throw new IllegalStateException("FooBar for repo " + repoName + " pr " + title, e);
            }
        };
    }

    private Predicate<PullRequest> includeMergedBy(String user) {
        return pullRequest -> pullRequest.isMerged() && pullRequest.getMergedBy().getLogin().equalsIgnoreCase(user);
    }

    private Predicate<PullRequest> includePullRequestsBy(String user) {
        return pullRequest -> pullRequest.getUser().getLogin().equalsIgnoreCase(user);
    }

    private Function<PullRequest, Stream<CommitComment>> getAllComments() {
        return pullRequest -> {
            Repository repo = pullRequest.getBase().getRepo();
            try {
                return pullRequestService.getComments(repo, pullRequest.getNumber()).stream();
            } catch (IOException e) {
                String repoName = repo.getName();
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

    private Predicate<PullRequest> pullRequestCreatedBetween(LocalDate startDate, LocalDate endDate) {
        return pullRequest -> {
            LocalDate createdAt = convertToLocalDate(pullRequest.getCreatedAt());
            return createdAt.isAfter(startDate.minusDays(1))
                    && createdAt.isBefore(endDate.plusDays(1));
        };
    }

    private Predicate<PullRequest> excludePullRequestsBy(String user) {
        return pullRequest -> !pullRequest.getUser().getLogin().equalsIgnoreCase(user);
    }

    private Predicate<CommitComment> includeCommentsBy(String user) {
        return comment -> comment.getUser().getLogin().equalsIgnoreCase(user);
    }

}
