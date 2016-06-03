package com.novoda.github.reports.batch.persistence.converter;

import java.util.List;
import java.util.stream.Collectors;

public interface Converter<From, To> {

    To convertFrom(From from);

    default List<To> convertListFrom(List<From> repositories) {
        return repositories.stream()
                .map(this::convertFrom)
                .collect(Collectors.toList());
    }

}
