package com.novoda.reports;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.novoda.reports.pullrequest.PullRequestDatabase;

public class GitHubReportsLambda implements RequestHandler<GitHubReportsLambda.LambdaRequest, GitHubReportsLambda.LambdaResponse> {

    @Override
    public LambdaResponse handleRequest(LambdaRequest input, Context context) {
        NewLineLogger logger = new NewLineLogger(context.getLogger());
        logger.log("start");
        PullRequestDatabase pullRequestDatabase = new RdsMySqlPullRequestDatabase();
        logger.log("started");
        pullRequestDatabase.open();
        logger.log("opened");
        pullRequestDatabase.create();
        logger.log("created");
        pullRequestDatabase.close();
        logger.log("closed");

        return new LambdaResponse("Yeah it worked");
    }

    public static class LambdaRequest {

        public LambdaRequest() {

        }
    }

    public static class LambdaResponse {
        private String result;

        public LambdaResponse(String result) {
            this.result = result;
        }

        public LambdaResponse() {

        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }
}
