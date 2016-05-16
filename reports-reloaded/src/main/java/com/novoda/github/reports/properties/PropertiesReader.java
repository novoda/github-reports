package com.novoda.github.reports.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class PropertiesReader {

    private Properties properties;
    private String fileName;

    static PropertiesReader newInstance(String fileName) {
        return new PropertiesReader(new Properties(), fileName);
    }

    PropertiesReader(Properties properties, String fileName) {
        this.properties = properties;
        this.fileName = fileName;
    }

    String readProperty(String key) {
        InputStream inputStream = getInputStream(fileName);
        if (inputStream == null) {
            return null;
        }

        loadInputStream(inputStream);
        String property = properties.getProperty(key);
        closeInputStream(inputStream);

        return property;
    }

    private InputStream getInputStream(String fileName) {
        try {
            return new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadInputStream(InputStream inputStream) {
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
