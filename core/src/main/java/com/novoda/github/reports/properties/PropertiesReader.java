package com.novoda.github.reports.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private static final String TRUE_VALUE = "true";

    private final String fileName;
    private Properties properties;

    public static PropertiesReader newInstance(String fileName) {
        return new PropertiesReader(fileName);
    }

    private PropertiesReader(String fileName) {
        this.fileName = fileName;
    }

    public String readProperty(String key) {
        if (properties == null) {
            initProperties();
        }
        return properties.getProperty(key);
    }

    public boolean readPropertyAsBoolean(String key) {
        String value = readProperty(key);
        return TRUE_VALUE.equals(value);
    }

    private void initProperties() {
        properties = new Properties();
        try {
            InputStream inputStream = getInputStream(fileName);
            loadInputStream(inputStream);
            closeInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getInputStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException(String.format("Could not read the file \"%s\" in the resources folder.", fileName));
        }
        return inputStream;
    }

    private void loadInputStream(InputStream inputStream) throws IOException {
        properties.load(inputStream);
    }

    private void closeInputStream(InputStream inputStream) throws IOException {
        inputStream.close();
    }

    public Integer readPropertyAsInt(String key) {
        String value = readProperty(key);
        return Integer.valueOf(value);
    }
}
