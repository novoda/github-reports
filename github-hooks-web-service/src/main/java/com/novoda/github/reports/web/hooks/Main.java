package com.novoda.github.reports.web.hooks;

import com.amazonaws.util.StringInputStream;
import com.novoda.github.reports.web.hooks.lambda.PostGithubWebhookEventHandler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        extractingMain();
    }

    private static void extractingMain() throws IOException, URISyntaxException {

        String json = readFile("mapped_request.sample.json");

        PostGithubWebhookEventHandler handler = new PostGithubWebhookEventHandler();
        handler.handleRequest(new StringInputStream(json), new ByteArrayOutputStream(), null);
    }

    private static String readFile(String fileName) throws URISyntaxException, IOException {
        URL url = Main.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }
        Path path = Paths.get(url.toURI());
        return Files.lines(path).collect(Collectors.joining("\n"));
    }

    private static void encodingMain() {
        String secretToken = "n0v0d4";
        String encoded = "";

        try {
            encoded = encode(secretToken, "rui miguel vaz teixeira");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        System.out.println(encoded);
    }

    private static String encode(String key, String data) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha1HMAC = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
        sha1HMAC.init(secretKey);

        return Hex.encodeHexString(sha1HMAC.doFinal(data.getBytes("UTF-8")));
    }

}
