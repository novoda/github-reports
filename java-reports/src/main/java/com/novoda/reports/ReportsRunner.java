package com.novoda.reports;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.organisation.OrganisationRepoFinder;
import com.novoda.reports.pullrequest.PullRequestFinder;
import com.novoda.reports.pullrequest.comment.CommentFinder;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportsRunner {

    public static void main(String[] args) throws Exception {
        String githubAccessToken = args[0];
        boolean clearCaches = args.length == 1 || args[1] == null ? false : Boolean.valueOf(args[1]);
        // TODO input args
        List<String> users = new ArrayList<>();
//        users.add("blundell");
        users.add("mr_archano");
//        users.add("ataulm");

        String organisation = "Novoda";
        LocalDate startDate = LocalDate.parse("2016-01-02");
        LocalDate endDate = LocalDate.parse("2016-02-03");

        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(githubAccessToken);
        RepositoryService repositoryService = new RepositoryService(client);
        RateLimitRetryer rateLimitRetryer = new RateLimitRetryer(client);
        OrganisationRepoFinder organisationRepoFinder = OrganisationRepoFinder.newInstance(organisation, repositoryService, rateLimitRetryer);
        if (clearCaches) {
            organisationRepoFinder.clearCache();
        }
        List<OrganisationRepo> organisationRepos = organisationRepoFinder.getOrganisationRepositories();
        PullRequestService pullRequestService = new PullRequestService(client);
        PullRequestFinder pullRequestFinder = PullRequestFinder.newInstance(pullRequestService, rateLimitRetryer);
        CommentFinder commentFinder = CommentFinder.newInstance(pullRequestService, rateLimitRetryer);
        ReportTracker reportTracker = new ReportTracker(organisationRepos, pullRequestFinder, commentFinder);

        for (String user : users) {
            Report report = reportTracker.track(user, startDate, endDate);
            System.out.println(report);
        }
//        System.out.println(repositories);
    }


}
