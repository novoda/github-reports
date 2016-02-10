package com.novoda.reports.pullrequest;

class FullConverter {

    private final LiteConverter liteConverter;

    FullConverter(LiteConverter liteConverter) {
        this.liteConverter = liteConverter;
    }

    public FullPullRequest convert(org.eclipse.egit.github.core.PullRequest pullRequest) {
        LitePullRequest litePullRequest = liteConverter.convert(pullRequest);
        boolean isMerged = pullRequest.isMerged();
        String mergedByUserLogin = pullRequest.getMergedBy().getLogin();
        return new FullPullRequest(litePullRequest, isMerged, mergedByUserLogin);
    }

}
