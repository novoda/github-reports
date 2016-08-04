package com.novoda.github.reports.service.persistence.converter;

public class ConverterException extends Exception {
    public ConverterException(Exception e) {
        super("Conversion error: " + e);
    }
}
