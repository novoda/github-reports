package com.novoda.reports;

import com.novoda.reports.organisation.OrganisationRepo;
import com.novoda.reports.organisation.OrganisationRepoFinder;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reports {

    public static void main(String[] args) throws Exception {
        String githubAccessToken = args[0];
        // TODO input args
        List<String> users = new ArrayList<>();
        users.add("blundell");
//        users.add("mr_archano");
//        users.add("ataulm");

        String organisation = "Novoda";
        LocalDate startDate = LocalDate.parse("2016-02-02");
        LocalDate endDate = LocalDate.parse("2016-02-03");

        // Re-write the ruby reports in java
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(githubAccessToken);
        RepositoryService repositoryService = new RepositoryService(client);
        OrganisationRepoFinder organisationRepoFinder = OrganisationRepoFinder.newInstance(organisation, repositoryService);
        List<OrganisationRepo> organisationRepos = organisationRepoFinder.getOrganisationRepositories();
        PullRequestService pullRequestService = new PullRequestService(client);
        PullRequestTracker pullRequestTracker = new PullRequestTracker(organisationRepos, pullRequestService);

        for (String user : users) {
            Report report = pullRequestTracker.track(user, startDate, endDate);
            System.out.println(report);
        }
//        System.out.println(repositories);
    }


}
