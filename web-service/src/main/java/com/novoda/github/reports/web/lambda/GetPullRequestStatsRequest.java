package com.novoda.github.reports.web.lambda;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.novoda.github.reports.data.EventDataLayer;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class GetPullRequestStatsRequest {

    public static GetPullRequestStatsRequest.Builder builder() {
        return new AutoValue_GetPullRequestStatsRequest.Builder();
    }

    public static TypeAdapter<GetPullRequestStatsRequest> typeAdapter(Gson gson) {
        return new AutoValue_GetPullRequestStatsRequest.GsonTypeAdapter(gson);
    }

    @Nullable
    abstract Date from();

    @Nullable
    abstract Date to();

    @Nullable
    abstract List<String> repos();

    @Nullable
    abstract EventDataLayer.PullRequestStatsGroupBy groupBy();

    @Nullable
    abstract Boolean withAverage();

    public Builder toBuilder() {
        return new AutoValue_GetPullRequestStatsRequest.Builder(this);
    }

    @AutoValue.Builder
    public static abstract class Builder {
        abstract Builder from(@Nullable Date from);

        abstract Builder to(@Nullable Date to);

        abstract Builder repos(@Nullable List<String> repos);

        abstract Builder groupBy(@Nullable EventDataLayer.PullRequestStatsGroupBy groupBy);

        abstract Builder withAverage(@Nullable Boolean withAverage);

        abstract GetPullRequestStatsRequest build();
    }

}
