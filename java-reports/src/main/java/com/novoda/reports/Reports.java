package com.novoda.reports;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reports {

    public static void main(String[] args) throws IOException {
        String githubAccessToken = args[0];
        // TODO input args
        List<String> users = new ArrayList<>();
        users.add("blundell");
//        users.add("mr_archano");
//        users.add("ataulm");

        String organisation = "Novoda";
        LocalDate startDate = LocalDate.parse("2016-01-02");
        LocalDate endDate = LocalDate.parse("2016-01-03");

        // Re-write the ruby reports in java
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(githubAccessToken);
        RepositoryService repositoryService = new RepositoryService(client);
        PullRequestService pullRequestService = new PullRequestService(client);
        PullRequestTracker pullRequestTracker = new PullRequestTracker(organisation, repositoryService, pullRequestService);

        for (String user : users) {
            PullRequestTracker.Report report = pullRequestTracker.track(user, startDate, endDate);
            System.out.println(report);
        }
    }


}
