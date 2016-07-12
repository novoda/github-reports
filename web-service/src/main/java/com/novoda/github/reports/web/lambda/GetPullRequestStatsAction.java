package com.novoda.github.reports.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.model.PullRequestStats;
import com.novoda.github.reports.reader.UsersServiceClient;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.*;
import java.util.List;

public class GetPullRequestStatsAction implements RequestStreamHandler {

    private static final String ISO_8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .setDateFormat(ISO_8601_DATE_TIME_FORMAT)
            .create();

    private final UsersServiceClient userServiceClient;
    private final EventDataLayer eventDataLayer;

    public GetPullRequestStatsAction() {
        userServiceClient = UsersServiceClient.newInstance();
        ConnectionManager connectionManager = DbConnectionManager.newInstance();
        eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Reader reader = new InputStreamReader(input);
        GetPullRequestStatsRequest request = gson.fromJson(reader, GetPullRequestStatsRequest.class);
        List<String> users = userServiceClient
                .getAllGithubUsers()
                .toList()
                .toBlocking()
                .first();

        try {
            PullRequestStats organisationStats = eventDataLayer.getOrganisationStats(
                    request.from(),
                    request.to(),
                    request.repos(),
                    users,
                    request.groupBy(),
                    request.withAverage());

            String json = gson.toJson(organisationStats);
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(json);
            writer.close();
        } catch (DataLayerException e) {
            throw new RuntimeException(e);
        }
    }
}
