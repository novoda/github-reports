package com.novoda.github.reports.web.hooks.secret;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

class HashSignatureCreator {

    private static final String HMAC_SHA_1_ALGORITHM = "HmacSHA1";

    private final SecretPropertiesReader secretPropertiesReader;

    public static HashSignatureCreator newInstance() {
        SecretPropertiesReader secretPropertiesReader = SecretPropertiesReader.newInstance();
        return new HashSignatureCreator(secretPropertiesReader);
    }

    private HashSignatureCreator(SecretPropertiesReader secretPropertiesReader) {
        this.secretPropertiesReader = secretPropertiesReader;
    }

    String createSignatureFor(String payload) throws InvalidSecretException {
        String secret = secretPropertiesReader.getSecret();
        return "sha1=" + signatureWith(secret, payload);
    }

    private String signatureWith(String secret, String payload) throws InvalidSecretException {
        Mac sha1Hmac = getHmacSha1();

        SecretKeySpec secretKey = new SecretKeySpec(getUtf8EncodedBytes(secret), HMAC_SHA_1_ALGORITHM);

        initMac(sha1Hmac, secretKey);

        return Hex.encodeHexString(sha1Hmac.doFinal(getUtf8EncodedBytes(payload)));
    }

    private Mac getHmacSha1() throws InvalidSecretException {
        try {
            return Mac.getInstance(HMAC_SHA_1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new InvalidSecretException(e);
        }
    }

    private byte[] getUtf8EncodedBytes(String value) throws InvalidSecretException {
        try {
            return value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InvalidSecretException(e);
        }
    }

    private void initMac(Mac sha1Hmac, SecretKeySpec secretKey) throws InvalidSecretException {
        try {
            sha1Hmac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new InvalidSecretException(e);
        }
    }

}
