package com.novoda.github.reports.data.model;

import com.google.auto.value.AutoValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AutoValue
public abstract class AggregatedStats implements Stats {

    private static final String NEW_LINE = "\n";

    public static Builder builder() {
        return new AutoValue_AggregatedStats.Builder();
    }

    abstract Map<String, AggregatedUserStats> usersStats();

    @Override
    public String describeStats() {
        return usersStats().entrySet()
                .stream()
                .map(userEntryToString())
                .collect(Collectors.joining(NEW_LINE));
    }

    private Function<Map.Entry<String, AggregatedUserStats>, String> userEntryToString() {
        return userEntry -> String.format(
                "- %s%s%s%s",
                userEntry.getKey(),
                NEW_LINE,
                describeAggregatedUserStats(userEntry.getValue()),
                NEW_LINE
        );
    }

    private String describeAggregatedUserStats(AggregatedUserStats stats) {
        return Arrays
                .stream(stats.describeStats().split(NEW_LINE))
                .map(s -> "  " + s)
                .collect(Collectors.joining(NEW_LINE));
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder usersStats(Map<String, AggregatedUserStats> usersStats);

        public abstract AggregatedStats build();

    }

}
