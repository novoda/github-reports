package com.novoda.github.reports.github.repository;

import com.novoda.github.reports.github.GithubRequestListener;

import java.util.List;

public interface RepositoriesListener extends GithubRequestListener<List<Repository>> {
}
