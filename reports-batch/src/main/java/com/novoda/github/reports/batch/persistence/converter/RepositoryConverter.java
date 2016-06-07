package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.repository.Repository;
import com.novoda.github.reports.data.model.DatabaseRepository;

public class RepositoryConverter implements Converter<Repository, DatabaseRepository> {

    public static Converter<Repository, DatabaseRepository> newInstance() {
        return new RepositoryConverter();
    }

    @Override
    public DatabaseRepository convertFrom(Repository repository) {
        return DatabaseRepository.create(
                repository.getId(),
                repository.getName(),
                repository.isPrivateRepo()
        );
    }

}
