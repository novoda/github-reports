package com.novoda.github.reports.service.persistence.converter;

import java.util.ArrayList;
import java.util.List;

public interface Converter<From, To> {

    To convertFrom(From from) throws ConverterException;

    default List<To> convertListFrom(List<From> elements) throws ConverterException {
        List<To> list = new ArrayList<>(elements.size());
        for (From element : elements) {
            list.add(convertFrom(element));
        }
        return list;
    }

}
