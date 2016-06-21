package com.novoda.github.reports.service.persistence.converter;

public class ConverterException extends Throwable {
    ConverterException(Throwable e) {
        super("Conversion error.", e);
    }
}
