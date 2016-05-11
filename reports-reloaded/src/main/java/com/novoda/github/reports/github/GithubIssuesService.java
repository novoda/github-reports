package com.novoda.github.reports.github;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubIssuesService {

    private IssueService issueService;
    private IRepositoryIdProvider repositoryIdProvider;

    public static GithubIssuesService newInstance(String repo) {
        RepositoryName repositoryName = new RepositoryName(repo);
        IssueService issueService = new IssueService(ClientContainer.INSTANCE.getClient());
        return new GithubIssuesService(issueService, repositoryName);
    }

    GithubIssuesService(IssueService issueService, IRepositoryIdProvider repositoryIdProvider) {
        this.issueService = issueService;
        this.repositoryIdProvider = repositoryIdProvider;
    }

    public List<Issue> getIssues(Fields fields) {
        try {
            return issueService.getIssues(repositoryIdProvider, fields.toMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> getAllIssues() {
        try {
            return issueService.getIssues(repositoryIdProvider, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Issue> getIssuesCreatedBy(String user) {
        return getIssues(new Fields().createdBy(user));
    }

    public List<Issue> getIssuesSince(String date) {
        // TODO Date class
        return getIssues(new Fields().since(date));
    }

    public static class Fields {

        private static final String FILTER_CREATOR = "creator";

        private Map<String, String> fields = new HashMap<>();

        Map<String, String> toMap() {
            return fields;
        }

        public Fields createdBy(String user) {
            fields.put(FILTER_CREATOR, user);
            return this;
        }

        public Fields mentioning(String user) {
            fields.put(IssueService.FILTER_MENTIONED, user);
            return this;
        }

        public Fields stateIs(State state) {
            fields.put(IssueService.FILTER_STATE, state.toString());
            return this;
        }

        public Fields since(String date) { // ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.
            fields.put(IssueService.FIELD_SINCE, date);
            return this;
        }
    }
}
