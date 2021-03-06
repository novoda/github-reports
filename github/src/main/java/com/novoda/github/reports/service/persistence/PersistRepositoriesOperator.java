package com.novoda.github.reports.service.persistence;

import com.novoda.github.reports.service.persistence.converter.Converter;
import com.novoda.github.reports.service.repository.GithubRepository;
import com.novoda.github.reports.data.DataLayer;
import com.novoda.github.reports.data.RepoDataLayer;
import com.novoda.github.reports.data.model.Repository;

class PersistRepositoriesOperator extends PersistOperator<GithubRepository, Repository> {

    public static PersistRepositoriesOperator newInstance(RepoDataLayer repoDataLayer,
                                                          Converter<GithubRepository, Repository> converter) {

        return new PersistRepositoriesOperator(repoDataLayer, converter);
    }

    private PersistRepositoriesOperator(DataLayer<Repository> dataLayer,
                                        Converter<GithubRepository, Repository> converter) {
        super(dataLayer, converter);
    }

}
