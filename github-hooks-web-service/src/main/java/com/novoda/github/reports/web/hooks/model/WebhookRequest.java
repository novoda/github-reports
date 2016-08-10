package com.novoda.github.reports.web.hooks.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

//@AutoValue
public class WebhookRequest {

//    public static WebhookRequest.Builder builder() {
//        return new AutoValue_WebhookRequest.Builder();
//    }

//    public static TypeAdapter<WebhookRequest> typeAdapter(Gson gson) {
//        return new AutoValue_WebhookRequest.GsonTypeAdapter(gson);
//    }

    @SerializedName("body")
    private GithubWebhookEvent event;

    private Map<String, String> headers;

    public GithubWebhookEvent getEvent() {
        return event;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }


}
