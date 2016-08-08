package com.novoda.github.reports.web.hooks;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class Main {

    public static void main(String[] args) {

        String secretToken = "n0v0d4";
        String encoded = "";

        try {
            encoded = encode(secretToken, "rui miguel vaz teixeira");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        System.out.println(encoded);
    }

    public static String encode(String key, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha1HMAC = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
        sha1HMAC.init(secretKey);

        return Hex.encodeHexString(sha1HMAC.doFinal(data.getBytes("UTF-8")));
    }

}
