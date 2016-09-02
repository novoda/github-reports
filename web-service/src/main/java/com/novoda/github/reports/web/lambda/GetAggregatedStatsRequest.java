package com.novoda.github.reports.web.lambda;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class GetAggregatedStatsRequest {

    public static GetAggregatedStatsRequest.Builder builder() {
        return new AutoValue_GetAggregatedStatsRequest.Builder();
    }

    public static TypeAdapter<GetAggregatedStatsRequest> typeAdapter(Gson gson) {
        return new AutoValue_GetAggregatedStatsRequest.GsonTypeAdapter(gson);
    }

    @Nullable
    public abstract Date from();

    @Nullable
    public abstract Date to();

    @Nullable
    public abstract String timezone();

    @Nullable
    public abstract List<String> users();

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder from(@Nullable Date from);

        public abstract Builder to(@Nullable Date to);

        public abstract Builder timezone(@Nullable String timezone);

        public abstract Builder users(@Nullable List<String> users);

        public abstract GetAggregatedStatsRequest build();

    }

}
