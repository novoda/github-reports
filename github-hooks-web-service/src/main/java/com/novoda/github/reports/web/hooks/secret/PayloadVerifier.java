package com.novoda.github.reports.web.hooks.secret;

import com.novoda.github.reports.web.hooks.model.WebhookRequest;

public class PayloadVerifier {

    private final SecretSignatureExtractor secretSignatureExtractor;
    private final HashSignatureCreator hashSignatureCreator;

    public static PayloadVerifier newInstance() {
        SecretSignatureExtractor secretSignatureExtractor = new SecretSignatureExtractor();
        HashSignatureCreator hashSignatureCreator = HashSignatureCreator.newInstance();
        return new PayloadVerifier(secretSignatureExtractor, hashSignatureCreator);
    }

    private PayloadVerifier(SecretSignatureExtractor secretSignatureExtractor, HashSignatureCreator hashSignatureCreator) {
        this.secretSignatureExtractor = secretSignatureExtractor;
        this.hashSignatureCreator = hashSignatureCreator;
    }

    public void checkIfPayloadIsValid(WebhookRequest request) throws SecretException {
        String json = request.body().toString();
        String localSignature = hashSignatureCreator.createSignatureFor(json);
        String originalSignature = secretSignatureExtractor.extractSignatureFrom(request);
        if (originalSignature.equals(localSignature)) {
            throw new SecurityException("Signatures do not match.");
        }
    }
}
