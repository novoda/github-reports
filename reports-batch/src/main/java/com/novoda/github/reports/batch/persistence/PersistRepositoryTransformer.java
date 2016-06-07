package com.novoda.github.reports.batch.persistence;

import com.novoda.github.reports.batch.persistence.converter.Converter;
import com.novoda.github.reports.batch.repository.GithubRepository;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.Repository;

public class PersistRepositoryTransformer extends PersistTransformer<GithubRepository, Repository> {

    private static final int REPOSITORY_BUFFER_SIZE = 100;

    public static PersistRepositoryTransformer newInstance(RepoDataLayer repoDataLayer, Converter<GithubRepository, Repository> converter) {
        PersistRepositoriesOperator operator = PersistRepositoriesOperator.newInstance(repoDataLayer, converter);
        return new PersistRepositoryTransformer(operator, REPOSITORY_BUFFER_SIZE);
    }

    private PersistRepositoryTransformer(PersistOperator<GithubRepository, Repository> operator, int bufferSize) {
        super(operator, bufferSize);
    }

}
