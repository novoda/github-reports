package com.novoda.github.reports.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.List;

public class GetOrganisationUsersAction implements RequestHandler<Void, List<String>> {

    private final UsersServiceClient userServiceClient;

    public GetOrganisationUsersAction() {
        userServiceClient = UsersServiceClient.newInstance();
    }

    @Override
    public List<String> handleRequest(Void input, Context context) {
        return userServiceClient.getAllGithubUsers().toList().toBlocking().first();
    }

}
