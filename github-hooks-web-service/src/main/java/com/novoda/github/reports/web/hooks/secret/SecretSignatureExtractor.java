package com.novoda.github.reports.web.hooks.secret;

import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.util.Map;

class SecretSignatureExtractor {

    String extractSignatureFrom(WebhookRequest request) throws SecretException {

        Map<String, String> headers = request.headers();
        String signature = headers.get("X-Hub-Signature");

        if (signature == null) {
            throw new SecretException("Request does not contain a signature.");
        }

        return signature;
    }

}
