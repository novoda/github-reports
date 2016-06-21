package com.novoda.github.reports.service.persistence.converter;

import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.data.model.Repository;

public class RepositoryConverter implements Converter<GithubRepository, Repository> {

    public static Converter<GithubRepository, Repository> newInstance() {
        return new RepositoryConverter();
    }

    @Override
    public Repository convertFrom(GithubRepository repository) {
        return Repository.create(
                repository.getId(),
                repository.getName(),
                repository.isPrivateRepo()
        );
    }

}
