package com.novoda.github.reports.web.hooks.lambda;

import com.novoda.github.reports.web.hooks.model.WebhookRequest;

import java.util.Map;

public class SecretSignatureExtractor {

    private OutputWriter outputWriter;

    SecretSignatureExtractor(OutputWriter outputWriter) {
        this.outputWriter = outputWriter;
    }

    String extractSignatureFrom(WebhookRequest request) {

        Map<String, String> headers = request.headers();
        String signature = headers.get("X-Hub-Signature");

        if (signature == null) {
            outputWriter.outputException(new SecurityException("h4x0r3d"));
        }

        return signature;
    }

}
