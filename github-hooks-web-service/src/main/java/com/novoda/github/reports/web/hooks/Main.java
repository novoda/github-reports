package com.novoda.github.reports.web.hooks;

import com.amazonaws.util.StringInputStream;
import com.novoda.github.reports.web.hooks.lambda.PostGithubWebhookEventHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {

        String json = readFile("pull_request_opened.sample.json");

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

}
