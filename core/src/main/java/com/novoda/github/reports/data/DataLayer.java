package com.novoda.github.reports.data;

import java.util.List;

public interface DataLayer<T> {

    T updateOrInsert(T element) throws DataLayerException;

    List<T> updateOrInsert(List<T> elements) throws DataLayerException;

}
