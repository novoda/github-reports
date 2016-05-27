package com.novoda.github.reports.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private final Properties properties;

    public static PropertiesReader newInstance(String fileName) {
        return new PropertiesReader(fileName);
    }

    private PropertiesReader(String fileName) {
        this.properties = new Properties();
        try {
            InputStream inputStream = getInputStream(fileName);
            loadInputStream(inputStream);
            closeInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readProperty(String key) {
        return properties.getProperty(key);
    }

    private InputStream getInputStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(fileName);
    }

    private void loadInputStream(InputStream inputStream) throws IOException {
        properties.load(inputStream);
    }

    private void closeInputStream(InputStream inputStream) throws IOException {
        inputStream.close();
    }
}
