package com.novoda.reports;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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
        List<PullRequest> prs = getAllPullRequests(repositories)
                .filter(isWithin(startDate, endDate))
                .collect(Collectors.toList());
        System.out.println(prs);
        return prs
                .parallelStream()
                .map(pullRequest -> {
                    Repository repo = pullRequest.getHead().getRepo();
                    try {
                        PullRequest pullRequestMaybeMore = pullRequestService.getPullRequest(repo, pullRequest.getNumber());
                        System.out.println(pullRequestMaybeMore.getMergedBy().getName());
                        return pullRequestMaybeMore;
                    } catch (IOException e) {
                        String repoName = repo.getName();
                        String title = pullRequest.getTitle();
                        throw new IllegalStateException("FooBar for repo " + repoName + " pr " + title, e);
                    }
                })
                .filter(pullRequest -> pullRequest.getMergedBy().getLogin().equalsIgnoreCase(user))
                .count();
    }

    private long getCreatedPrsCount(String user, LocalDate startDate, LocalDate endDate, List<Repository> repositories) {
        return getAllPullRequests(repositories)
                .filter(pullRequest -> pullRequest.getUser().getLogin().equalsIgnoreCase(user))
                .filter(isWithin(startDate, endDate))
                .count();
    }

    private Stream<PullRequest> getAllPullRequests(List<Repository> repositories) {
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

    private Predicate<PullRequest> isWithin(LocalDate startDate, LocalDate endDate) {
        return pullRequest -> {
            LocalDate createdAt = pullRequest
                    .getCreatedAt()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return createdAt.isAfter(startDate.minusDays(1))
                    && createdAt.isBefore(endDate.plusDays(1));
        };
    }

    public static class Report {
        private final String user;
        private final long mergedPrs;
        private final long createdPrs;

        public Report(String user, long mergedPrs, long createdPrs) {
            this.user = user;
            this.mergedPrs = mergedPrs;
            this.createdPrs = createdPrs;
        }

        @Override
        public String toString() {
            return "User " + user + " merged " + mergedPrs + " PRs.\n" +
                    "User " + user + " created " + createdPrs + " PRs.\n";
        }

        public static class Builder {
            private final String user;
            private long mergedPrs;
            private long createdPrs;

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

            public Report build() {
                return new Report(user, mergedPrs, createdPrs);
            }
        }
    }
}
