package com.novoda.github.reports.web.hooks.secret;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class HashSignatureCreator {

    private final SecretPropertiesReader secretPropertiesReader;

    public HashSignatureCreator(SecretPropertiesReader secretPropertiesReader) {
        this.secretPropertiesReader = secretPropertiesReader;
    }

    String createSignatureFor(String payload) throws SecretException {
        String secret = secretPropertiesReader.getSecret();
        return signatureWith(secret, payload);
    }

    private String signatureWith(String secret, String payload) throws SecretException {
        Mac sha1Hmac = getHmacSha1();

        SecretKeySpec secretKey = new SecretKeySpec(getUtf8EncodedBytes(secret), "HmacSHA1");

        initMac(sha1Hmac, secretKey);

        return Hex.encodeHexString(sha1Hmac.doFinal(getUtf8EncodedBytes(payload)));
    }

    private Mac getHmacSha1() throws SecretException {
        try {
            return Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new SecretException(e);
        }
    }

    private byte[] getUtf8EncodedBytes(String value) throws SecretException {
        try {
            return value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SecretException(e);
        }
    }

    private void initMac(Mac sha1Hmac, SecretKeySpec secretKey) throws SecretException {
        try {
            sha1Hmac.init(secretKey);
        } catch (InvalidKeyException e) {
            throw new SecretException(e);
        }
    }

}
