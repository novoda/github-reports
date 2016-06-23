package com.novoda.github.reports.floatschedule.convert;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonMapReader {

    private static final String DELIMITER = "\n";

    private final Gson gson;

    public static JsonMapReader newInstance() {
        return new JsonMapReader(new Gson());
    }

    JsonMapReader(Gson gson) {
        this.gson = gson;
    }

    public Map<String, String> readFromResource(String fileName) throws URISyntaxException, IOException  {

        URL url = JsonMapReader.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }

        Path path = Paths.get(url.toURI());
        String content = Files.lines(path).collect(Collectors.joining(DELIMITER));

        Map<String, String> map = new HashMap<>(0);
        return gson.fromJson(content, map.getClass());
    }


}
