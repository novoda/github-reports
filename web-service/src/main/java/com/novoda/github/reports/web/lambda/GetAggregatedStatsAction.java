package com.novoda.github.reports.web.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.novoda.floatschedule.FloatServiceClient;
import com.novoda.github.reports.data.DataLayerException;
import com.novoda.github.reports.data.EventDataLayer;
import com.novoda.github.reports.data.db.ConnectionManager;
import com.novoda.github.reports.data.db.DbConnectionManager;
import com.novoda.github.reports.data.db.DbEventDataLayer;
import com.novoda.github.reports.data.model.AggregatedStats;
import com.novoda.github.reports.data.model.UserAssignments;
import com.ryanharter.auto.value.gson.AutoValueGsonTypeAdapterFactory;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GetAggregatedStatsAction implements RequestStreamHandler {

    private static final String ISO_8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
            .registerTypeAdapter(TimeZone.class, new TimeZoneTypeAdapter())
            .setDateFormat(ISO_8601_DATE_TIME_FORMAT)
            .create();

    private final EventDataLayer eventDataLayer;
    private final FloatServiceClient floatServiceClient;

    public GetAggregatedStatsAction() {
        ConnectionManager connectionManager = DbConnectionManager.newInstance();
        this.eventDataLayer = DbEventDataLayer.newInstance(connectionManager);
        this.floatServiceClient = FloatServiceClient.newInstance();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Reader reader = new InputStreamReader(input);
        GetAggregatedStatsRequest request = gson.fromJson(reader, GetAggregatedStatsRequest.class);

        Map<String, List<UserAssignments>> usersAssignments = floatServiceClient.getGithubUsersAssignmentsInDateRange(
                request.users(),
                request.from(),
                request.to(),
                request.timezone()
        );

        try {
            AggregatedStats aggregatedStats = eventDataLayer.getAggregatedUserAssignmentsStats(
                    request.from(),
                    request.to(),
                    usersAssignments
            );
            String json = gson.toJson(aggregatedStats);
            OutputStreamWriter writer = new OutputStreamWriter(output);
            writer.write(json);
            writer.close();
        } catch (DataLayerException e) {
            e.printStackTrace();
        }
    }

}
