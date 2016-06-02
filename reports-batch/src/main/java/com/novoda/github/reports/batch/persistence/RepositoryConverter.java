package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.repository.Repository;

public class RepositoryConverter implements Converter<Repository, com.novoda.github.reports.data.model.Repository> {

    public static Converter<Repository, com.novoda.github.reports.data.model.Repository> newInstance() {
        return new RepositoryConverter();
    }

    @Override
    public com.novoda.github.reports.data.model.Repository convertFrom(Repository repository) {
        return com.novoda.github.reports.data.model.Repository.create(
                repository.getId(),
                repository.getName(),
                repository.isPrivateRepo()
        );

    }

}
