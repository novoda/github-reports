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

public class PullRequestTracker {

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
        List<PullRequest> prs = getAllPullRequestsIn(repositories)
                .filter(pullRequestCreatedBetween(startDate, endDate))
                .collect(Collectors.toList());
        for (PullRequest pr : prs) {
            System.out.println("PR " + pr.getTitle());
            System.out.println("created at " + pr.getCreatedAt());
            System.out.println("by " + pr.getUser().getLogin());
        }
        return prs
                .parallelStream()
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

    public static class Report {
        private final String user;
        private final long mergedPrs;
        private final long createdPrs;
        private final long otherPeopleComments;
        private final long usersComments;

        public Report(String user, long mergedPrs, long createdPrs, long otherPeopleComments, long usersComments) {
            this.user = user;
            this.mergedPrs = mergedPrs;
            this.createdPrs = createdPrs;
            this.otherPeopleComments = otherPeopleComments;
            this.usersComments = usersComments;
        }

        @Override
        public String toString() {
            return "User " + user + " merged " + mergedPrs + " PRs.\n" +
                    "User " + user + " created " + createdPrs + " PRs.\n" +
                    "People wrote " + otherPeopleComments + " comments in " + user + "'s PRs.\n" +
                    "User " + user + " wrote " + usersComments + " comments in other peoples PRs.\n";
        }

        public static class Builder {
            private final String user;
            private long mergedPrs;
            private long createdPrs;
            private long otherPeopleComments;
            private long usersComments;

            public Builder(String user) {
                this.user = user;
            }

            public Builder withMergedPullRequests(long count) {
                this.mergedPrs = count;
                return this;
            }

            public Builder withCreatedPullRequests(long count) {
                this.createdPrs = count;
                return this;
            }

            public Builder withOtherPeopleCommentsCount(long count) {
                otherPeopleComments = count;
                return this;
            }

            public Builder withUsersCommentCount(long count) {
                usersComments = count;
                return this;
            }

            public Report build() {
                return new Report(user, mergedPrs, createdPrs, otherPeopleComments, usersComments);
            }
        }
    }
}
