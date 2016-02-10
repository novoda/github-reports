package com.novoda.reports.pullrequest;

public class FullPullRequest {

    private final LitePullRequest litePullRequest;
    private final boolean isMerged;
    private final String mergedByUserLogin;

    public FullPullRequest(LitePullRequest litePullRequest, boolean isMerged, String mergedByUserLogin) {
        this.litePullRequest = litePullRequest;
        this.isMerged = isMerged;
        this.mergedByUserLogin = mergedByUserLogin;
    }

    public LitePullRequest getLitePullRequest() {
        return litePullRequest;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public String getMergedByUserLogin() {
        return mergedByUserLogin;
    }

    @Override
    public String toString() {
        return "FullPullRequest{" +
                "litePullRequest=" + litePullRequest +
                ", isMerged=" + isMerged +
                ", mergedByUserLogin='" + mergedByUserLogin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullPullRequest that = (FullPullRequest) o;

        return isMerged == that.isMerged &&
                litePullRequest.equals(that.litePullRequest) &&
                mergedByUserLogin.equals(that.mergedByUserLogin);

    }

    @Override
    public int hashCode() {
        int result = litePullRequest.hashCode();
        result = 31 * result + (isMerged ? 1 : 0);
        result = 31 * result + mergedByUserLogin.hashCode();
        return result;
    }
}
