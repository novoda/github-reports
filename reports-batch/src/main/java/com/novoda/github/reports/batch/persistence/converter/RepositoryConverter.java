package com.novoda.github.reports.batch.persistence.converter;

import com.novoda.github.reports.batch.repository.GithubRepository;
import com.novoda.github.reports.data.model.DatabaseRepository;

public class RepositoryConverter implements Converter<GithubRepository, DatabaseRepository> {

    public static Converter<GithubRepository, DatabaseRepository> newInstance() {
        return new RepositoryConverter();
    }

    @Override
    public DatabaseRepository convertFrom(GithubRepository repository) {
        return DatabaseRepository.create(
                repository.getId(),
                repository.getName(),
                repository.isPrivateRepo()
        );
    }

}
