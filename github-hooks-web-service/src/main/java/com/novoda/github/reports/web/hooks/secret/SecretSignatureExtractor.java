package com.novoda.github.reports.web.hooks.secret;

import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.util.Map;

public class SecretSignatureExtractor {

    public String extractSignatureFrom(WebhookRequest request) throws SecurityException {

        Map<String, String> headers = request.headers();
        String signature = headers.get("X-Hub-Signature");

        if (signature == null) {
            throw new SecurityException("Request does not contain a signature.");
        }

        return signature;
    }

}
