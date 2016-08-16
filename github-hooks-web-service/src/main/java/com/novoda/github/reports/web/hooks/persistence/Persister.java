package com.novoda.github.reports.web.hooks.persistence;

public interface Persister<T> {

    void persist(T event) throws PersistenceException;

}
