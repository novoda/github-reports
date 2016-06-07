package com.novoda.github.reports.batch.persistence.converter;

public class ConverterException extends Throwable {
    ConverterException(Throwable e) {
        super("Conversion error.", e);
    }
}
