package com.novoda.github.reports.reader;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class JsonMapReader<T> {

    private static final String DELIMITER = "\n";

    private final Gson gson;
    private final Class<T> classOfT;

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked"})
    static JsonMapReader<Map<String,String>> newStringToStringInstance() {
        Map<String, String> map = new HashMap<>(0);
        Class mapClass = map.getClass();
        return new JsonMapReader<>(new Gson(), mapClass);
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked"})
    static JsonMapReader<Map<String, List<String>>> newStringToListOfStringsInstance() {
        Map<String, List<String>> map = new HashMap<>(0);
        Class mapClass = map.getClass();
        return new JsonMapReader<>(new Gson(), mapClass);
    }

    private JsonMapReader(Gson gson, Class<T> classOfT) {
        this.gson = gson;
        this.classOfT = classOfT;
    }

    T readFromResource(String fileName) throws URISyntaxException, IOException {
        URL url = JsonMapReader.class.getClassLoader().getResource(fileName);
        if (url == null) {
            throw new FileNotFoundException(fileName + " was not found in the resources directory.");
        }

        Path path = Paths.get(url.toURI());
        String content = Files.lines(path).collect(Collectors.joining(DELIMITER));

        return gson.fromJson(content, classOfT);
    }

}
