package com.novoda.reports;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

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

    private final String organisation;
    private final RepositoryService repositoryService;
    private final PullRequestService pullRequestService;

    private List<PullRequest> allPullRequests;

    public PullRequestTracker(String organisation, RepositoryService repositoryService, PullRequestService pullRequestService) {
        this.repositoryService = repositoryService;
        this.pullRequestService = pullRequestService;
        this.organisation = organisation;
    }

    public Report track(String user, LocalDate startDate, LocalDate endDate) {
        Report.Builder reportBuilder = new Report.Builder(user);
        List<Repository> repositories = getOrganisationRepositories();
        long mergedPrsCount = getMergedPrsCount(user, startDate, endDate, repositories);
        reportBuilder.withMergedPullRequests(mergedPrsCount);
        long createdPrsCount = getCreatedPrsCount(user, startDate, endDate, repositories);
        reportBuilder.withCreatedPullRequests(createdPrsCount);
        long otherPeopleCommentsCount = getOtherPeopleCommentsCount(user, startDate, endDate, repositories);
        reportBuilder.withOtherPeopleCommentsCount(otherPeopleCommentsCount);
        long usersCommentCount = getUsersCommentsCount(user, startDate, endDate, repositories);
        reportBuilder.withUsersCommentCount(usersCommentCount);
        return reportBuilder.build();
    }

    private List<Repository> getOrganisationRepositories() {
        try {
            return repositoryService.getOrgRepositories(organisation);
        } catch (IOException e) {
            throw new IllegalStateException("Foo get repositories for " + organisation, e);
        }
    }

    private long getMergedPrsCount(String user, LocalDate startDate, LocalDate endDate, List<Repository> repositories) {
        return getAllPullRequestsIn(repositories)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .map(getFullDataPullRequest())
                .filter(pullRequest -> pullRequest.isMerged() && pullRequest.getMergedBy().getLogin().equalsIgnoreCase(user))
                .count();
    }

    private Stream<PullRequest> getAllPullRequestsIn(List<Repository> repositories) {
        if (allPullRequests == null) {
            allPullRequests = repositories
                    .parallelStream()
                    .flatMap(repository -> {
                        try {
                            return pullRequestService
                                    .getPullRequests(repository, "all")
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

    private long getCreatedPrsCount(String user, LocalDate startDate, LocalDate endDate, List<Repository> repositories) {
        return getAllPullRequestsIn(repositories)
                .filter(pullRequestIncludeBy(user))
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .count();
    }

    private Predicate<PullRequest> pullRequestIncludeBy(String user) {
        return pullRequest -> pullRequest.getUser().getLogin().equalsIgnoreCase(user);
    }

    private Predicate<CommitComment> commentIncludeBy(String user) {
        return comment -> comment.getUser().getLogin().equalsIgnoreCase(user);
    }

    private long getOtherPeopleCommentsCount(String user, LocalDate startDate, LocalDate endDate, List<Repository> repositories) {
        return getAllPullRequestsIn(repositories)
                .filter(pullRequestIncludeBy(user))
                .flatMap(getAllComments())
                .filter(commentExcludeBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
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

    private Predicate<CommitComment> commentExcludeBy(String user) {
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

    private long getUsersCommentsCount(String user, LocalDate startDate, LocalDate endDate, List<Repository> repositories) {
        return getAllPullRequestsIn(repositories)
                .filter(pullRequeastExcludeBy(user))
                .flatMap(getAllComments())
                .filter(commentIncludeBy(user))
                .filter(commentedBetween(startDate, endDate))
                .count();
    }

    private Predicate<PullRequest> pullRequeastExcludeBy(String user) {
        return pullRequest -> !pullRequest.getUser().getLogin().equalsIgnoreCase(user);
    }

}
