package com.novoda.contributions.githubcom;

import com.novoda.contributions.Contribution;
import com.novoda.reports.organisation.OrganisationRepoFinder;
import com.novoda.reports.pullrequest.PullRequestFinder;
import com.novoda.reports.pullrequest.comment.CommentFinder;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GitHubContributionTracker {

    private final OrganisationRepoFinder repoFinder;
    private final PullRequestFinder pullRequestFinder;
    private final CommentFinder commentFinder;

    public static GitHubContributionTracker newInstance(GitHubClient client, String organisation) {
        RepositoryService repositoryService = new RepositoryService(client);
        OrganisationRepoFinder repoFinder = OrganisationRepoFinder.newInstance(organisation, repositoryService);
        PullRequestService pullRequestService = new PullRequestService(client);
        PullRequestFinder pullRequestFinder = PullRequestFinder.newInstance(pullRequestService);
        CommentFinder commentFinder = CommentFinder.newInstance(pullRequestService);
        return new GitHubContributionTracker(repoFinder, pullRequestFinder, commentFinder);
    }

    public GitHubContributionTracker(OrganisationRepoFinder repoFinder, PullRequestFinder pullRequestFinder, CommentFinder commentFinder) {
        this.repoFinder = repoFinder;
        this.pullRequestFinder = pullRequestFinder;
        this.commentFinder = commentFinder;
    }

    /**
     * Find out if X developer has commented on / merged / closed another projects PR
     */
    public Contribution track(GitHubDev gitHubDev) {
        // Get all repositories for org
        long externalComments = repoFinder.getOrganisationRepositories()
                .stream()
                // Filter out those repositories that the person has worked on
                .filter(gitHubDev::hasNotWorkedOn)
                // Get pullrequests for non assigned repositories
                .flatMap(pullRequestFinder::streamLitePullRequests)
                // Get all comments
                .flatMap(commentFinder::streamComments)
                // Filter in comments by the person
                .filter(gitHubDev::wrote)
                // Count them
                .count();
        System.out.println(gitHubDev.getUsername() + " external comments " + externalComments);

        return new Contribution();
    }

}
