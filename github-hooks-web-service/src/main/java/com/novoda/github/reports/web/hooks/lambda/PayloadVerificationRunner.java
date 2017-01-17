package com.novoda.github.reports.web.hooks.lambda;

import com.novoda.github.reports.web.hooks.model.WebhookRequest;
import com.novoda.github.reports.web.hooks.secret.InvalidSecretException;
import com.novoda.github.reports.web.hooks.secret.PayloadVerifier;

class PayloadVerificationRunner {

    private final PayloadVerifier payloadVerifier;
    private final OutputWriter outputWriter;

    PayloadVerificationRunner(PayloadVerifier payloadVerifier, OutputWriter outputWriter) {
        this.payloadVerifier = payloadVerifier;
        this.outputWriter = outputWriter;
    }

    void verify(WebhookRequest request) throws RuntimeException {
        try {
            payloadVerifier.checkIfPayloadIsValid(request);
        } catch (InvalidSecretException e) {
            outputWriter.outputException(e);
        }
    }

}
