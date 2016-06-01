package com.novoda.github.reports.batch.persistence;

public interface Converter<From, To> {

    To convertFrom(From from);

}
