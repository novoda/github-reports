package com.novoda.github.reports.github;

import com.novoda.github.reports.properties.CredentialsReader;
import org.eclipse.egit.github.core.client.GitHubClient;

public enum ClientContainer {

    INSTANCE;

    private final GitHubClient gitHubClient = new GitHubClient();
    private boolean initialised = false;

    private void init() {
        if (initialised) {
            return;
        }
        CredentialsReader credentialsReader = CredentialsReader.newInstance();
        String token = credentialsReader.getAuthToken();
        gitHubClient.setOAuth2Token(token);
        initialised = true;
    }

    public GitHubClient getClient() {
        init();
        return gitHubClient;
    }

}
