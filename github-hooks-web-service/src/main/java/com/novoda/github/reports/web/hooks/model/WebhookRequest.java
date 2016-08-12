package com.novoda.github.reports.web.hooks.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

@AutoValue
public abstract class WebhookRequest {

    public static WebhookRequest.Builder builder() {
        return new AutoValue_WebhookRequest.Builder();
    }

    public static TypeAdapter<WebhookRequest> typeAdapter(Gson gson) {
        return new AutoValue_WebhookRequest.GsonTypeAdapter(gson);
    }

    @SerializedName("body")
    public abstract JsonObject body();

    @SerializedName("headers")
    public abstract Map<String, String> headers();

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder body(JsonObject body);

        public abstract Builder headers(Map<String, String> headers);

        public abstract WebhookRequest build();

    }
}
