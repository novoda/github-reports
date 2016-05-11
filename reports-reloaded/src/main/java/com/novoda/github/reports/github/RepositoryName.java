package com.novoda.github.reports.github;

import org.eclipse.egit.github.core.IRepositoryIdProvider;

public class RepositoryName implements IRepositoryIdProvider {

    private static final String ORGANISATION = "novoda";
    private final String name;

    public RepositoryName(String name) {
        this.name = name;
    }

    @Override
    public String generateId() {
        return ORGANISATION + "/" + name;
    }
}
