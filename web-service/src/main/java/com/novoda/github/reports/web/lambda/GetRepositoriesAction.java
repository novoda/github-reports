package com.novoda.github.reports.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.DbRepoDataLayer;
import com.novoda.github.reports.data.model.Repository;

import java.util.List;
import java.util.stream.Collectors;

public class GetRepositoriesAction implements RequestHandler<Void, List<String>> {

    private final DbRepoDataLayer repoDataLayer;

    public GetRepositoriesAction()  {
        DbConnectionManager connectionManager = DbConnectionManager.newInstance();
        repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
    }

    @Override
    public List<String> handleRequest(Void input, Context context) {
        try {
            return repoDataLayer.getRepositories().stream().map(Repository::name).collect(Collectors.toList());
        } catch (DataLayerException e) {
            throw new RuntimeException(e);
        }
    }

}
