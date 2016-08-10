package com.novoda.github.reports.web.hooks.secret;

import com.google.gson.Gson;
import com.novoda.github.reports.web.hooks.model.GithubWebhookEvent;
import com.novoda.github.reports.web.hooks.model.WebhookRequest;

public class PayloadVerifier {

    private final Gson gson;
    private final SecretSignatureExtractor secretSignatureExtractor;
    private final HashSignatureCreator hashSignatureCreator;

    public static PayloadVerifier newInstance(Gson gson) {
        SecretSignatureExtractor secretSignatureExtractor = new SecretSignatureExtractor();
        HashSignatureCreator hashSignatureCreator = HashSignatureCreator.newInstance();
        return new PayloadVerifier(gson, secretSignatureExtractor, hashSignatureCreator);
    }

    private PayloadVerifier(Gson gson, SecretSignatureExtractor secretSignatureExtractor, HashSignatureCreator hashSignatureCreator) {
        this.gson = gson;
        this.secretSignatureExtractor = secretSignatureExtractor;
        this.hashSignatureCreator = hashSignatureCreator;
    }


    public boolean checkIfPayloadIsValid(WebhookRequest request) throws SecretException {

        String originalSignature = secretSignatureExtractor.extractSignatureFrom(request);
        String json = getEventAsJsonFrom(request);
        String localSignature = "sha1=" + hashSignatureCreator.createSignatureFor(json);

        return originalSignature.equals(localSignature);
    }

    private String getEventAsJsonFrom(WebhookRequest request) throws SecretException {
        GithubWebhookEvent event = getEventFrom(request);
        return gson.toJson(event);
    }

    private GithubWebhookEvent getEventFrom(WebhookRequest request) throws SecretException {
        if (request.event() == null) {
            throw new SecretException("No event found in request body");
        }
        return request.event();
    }
}
